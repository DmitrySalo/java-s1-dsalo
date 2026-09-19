from __future__ import annotations

from dataclasses import dataclass
from datetime import UTC, datetime, timedelta
import json
from threading import Lock
from typing import Literal
from uuid import UUID, uuid4

import httpx
from pydantic import ValidationError

from app.agent.factory import AgentInvoker
from app.agent.intent import Intent
from app.agent.response import AgentResponse, Status, error_response
from app.agent.tools import authorize_fragrance_api_tool, create_fragrance_api_tool
from app.my_scents.client import MyScentsClient
from app.my_scents.errors import MyScentsApiError, MyScentsNetworkError
from app.my_scents.models import CreateFragrancePayload, UpdateFragrancePayload

_PREPARED_OPERATION_TTL = timedelta(minutes=5)
_MAX_PENDING_MUTATIONS = 100
_MAX_AGENT_STEPS = 10


@dataclass(frozen=True)
class PreparedMutation:
    intent: Intent
    expires_at: datetime


class CommandService:
    def __init__(self, agent: AgentInvoker, client: MyScentsClient, tool: object | None = None) -> None:
        self._agent = agent
        self._client = client
        self._tool = tool or create_fragrance_api_tool(client, requires_authorization=True)
        self._pending_mutations: dict[UUID, PreparedMutation] = {}
        self._pending_mutations_lock = Lock()

    def execute(
        self,
        message: str,
        confirmed: bool = False,
        prepared_operation_id: UUID | str | None = None,
    ) -> AgentResponse:
        self._remove_expired_mutations()
        if confirmed:
            return self._confirm_mutation(prepared_operation_id)
        try:
            intent = self._plan(message)
        except (ConnectionError, TimeoutError, httpx.HTTPError, ValidationError, ValueError, KeyError, RuntimeError):
            return error_response("help", "Could not safely determine a supported operation")
        if intent.action == "help":
            return AgentResponse(
                status=Status.SUCCESS,
                action="help",
                data={"operations": ["create", "get", "update"], "confirmation_required_for": ["create", "update"]},
            )
        if intent.action == "get":
            if intent.fragrance_id is None:
                return error_response("get", "A valid fragrance UUID is required")
            return self._run_tool(intent, "get")
        if intent.action == "update" and intent.fragrance_id is None:
            return error_response("update", "A valid fragrance UUID is required")
        if intent.fragrance is None:
            return error_response(intent.action, "Complete supported fragrance fields are required")
        operation_id = uuid4()
        with self._pending_mutations_lock:
            if len(self._pending_mutations) >= _MAX_PENDING_MUTATIONS:
                return error_response(intent.action, "Too many prepared operations; try again later")
            self._pending_mutations[operation_id] = PreparedMutation(
                intent=intent,
                expires_at=datetime.now(UTC) + _PREPARED_OPERATION_TTL,
            )
        return AgentResponse(
            status=Status.CONFIRMATION_REQUIRED,
            action=intent.action,
            data={
                "prepared_operation_id": str(operation_id),
                "operation": intent.action,
                "fragrance": intent.fragrance.model_dump(mode="json"),
            },
        )

    def _confirm_mutation(self, prepared_operation_id: UUID | str | None) -> AgentResponse:
        try:
            operation_id = UUID(str(prepared_operation_id))
        except (TypeError, ValueError):
            return error_response("help", "A prepared operation ID is required for confirmation")
        with self._pending_mutations_lock:
            pending = self._pending_mutations.pop(operation_id, None)
        if pending is None or pending.expires_at <= datetime.now(UTC):
            return error_response("help", "Prepared operation is missing or expired")
        return self._run_tool(pending.intent, pending.intent.action)

    def _remove_expired_mutations(self) -> None:
        now = datetime.now(UTC)
        with self._pending_mutations_lock:
            expired_ids = [operation_id for operation_id, pending in self._pending_mutations.items() if pending.expires_at <= now]
            for operation_id in expired_ids:
                del self._pending_mutations[operation_id]

    def _plan(self, message: str) -> Intent:
        result = self._agent.invoke(
            {"messages": [{"role": "user", "content": message}]},
            {"recursion_limit": _MAX_AGENT_STEPS},
        )
        structured_response = result.get("structured_response")
        if structured_response is not None:
            return Intent.model_validate(structured_response)
        messages = result.get("messages")
        if not isinstance(messages, list) or not messages:
            raise ValueError("Agent did not produce structured output")
        content = getattr(messages[-1], "content", None)
        if not isinstance(content, str):
            raise ValueError("Agent did not produce a JSON response")
        return Intent.model_validate(json.loads(content))

    def _run_tool(self, intent: Intent, operation: Literal["create", "get", "update"]) -> AgentResponse:
        try:
            with authorize_fragrance_api_tool():
                if operation == "get":
                    data = self._tool.invoke({"operation": "get", "fragrance_id": str(intent.fragrance_id)})
                elif operation == "create":
                    data = self._tool.invoke({"operation": "create", "fragrance": intent.fragrance.model_dump(mode="json")})
                else:
                    update_payload = UpdateFragrancePayload.model_validate(
                        {**intent.fragrance.model_dump(mode="json"), "id": str(intent.fragrance_id)}
                    )
                    data = self._tool.invoke({"operation": "update", "fragrance": update_payload.model_dump(mode="json")})
            return AgentResponse(status=Status.SUCCESS, action=operation, data=data)
        except MyScentsNetworkError:
            return error_response(operation, "my-scents is unavailable")
        except MyScentsApiError as error:
            return error_response(operation, f"my-scents returned HTTP {error.status_code}")
        except (ValidationError, ValueError):
            return error_response(operation, "Invalid tool input")

    def close(self) -> None:
        self._client.close()
