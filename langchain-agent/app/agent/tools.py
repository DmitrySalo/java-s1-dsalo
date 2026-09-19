from __future__ import annotations

from contextlib import contextmanager
from contextvars import ContextVar
import logging
import sys
from typing import Literal
from uuid import UUID

from langchain.tools import tool
from pydantic import BaseModel, ConfigDict

from app.my_scents.client import MyScentsClient
from app.my_scents.models import CreateFragrancePayload, FragranceResponse, UpdateFragrancePayload

LOGGER = logging.getLogger(__name__)
_tool_execution_authorized: ContextVar[bool] = ContextVar("tool_execution_authorized", default=False)
_TOOL_LOG_HANDLER_NAME = "safe_tool_console"


def configure_tool_logging() -> None:
    """Emit safe tool metadata to stderr for the documented local debug trace."""
    handler = next((handler for handler in LOGGER.handlers if handler.name == _TOOL_LOG_HANDLER_NAME), None)
    if handler is None:
        handler = logging.StreamHandler(sys.stderr)
        handler.name = _TOOL_LOG_HANDLER_NAME
        handler.setFormatter(logging.Formatter("%(message)s"))
        LOGGER.addHandler(handler)
    elif isinstance(handler, logging.StreamHandler):
        # pytest and redirected consoles can close the previous stderr between tool instances.
        handler.stream = sys.stderr
    LOGGER.setLevel(logging.INFO)
    LOGGER.propagate = False


class FragranceToolInput(BaseModel):
    """Narrow allowlisted input for the sole API tool."""

    model_config = ConfigDict(extra="forbid")

    operation: Literal["create", "get", "update"]
    fragrance_id: UUID | None = None
    fragrance: CreateFragrancePayload | UpdateFragrancePayload | None = None


@contextmanager
def authorize_fragrance_api_tool():
    """Allow the policy layer to execute the connected side-effect tool once."""
    token = _tool_execution_authorized.set(True)
    try:
        yield
    finally:
        _tool_execution_authorized.reset(token)


def create_fragrance_api_tool(client: MyScentsClient, requires_authorization: bool = False):
    """Create the only tool which can reach the public my-scents REST API."""
    configure_tool_logging()

    @tool(args_schema=FragranceToolInput)
    def fragrance_api(
        operation: Literal["create", "get", "update"],
        fragrance_id: UUID | None = None,
        fragrance: CreateFragrancePayload | UpdateFragrancePayload | None = None,
    ) -> dict[str, object]:
        """Execute an allowlisted fragrance operation against the public API."""
        if requires_authorization and not _tool_execution_authorized.get():
            raise ValueError("The application policy must authorize API tool execution")
        resource_id = str(fragrance_id or (fragrance.id if isinstance(fragrance, UpdateFragrancePayload) else ""))
        LOGGER.info("TOOL_CALL operation=%s method=%s path=/api/v1/fragrances resource_id=%s", operation, _method(operation), resource_id)
        if operation == "get":
            if fragrance_id is None:
                raise ValueError("fragrance_id is required for get")
            result = client.get_fragrance(str(fragrance_id))
        elif operation == "create":
            if fragrance is None or isinstance(fragrance, UpdateFragrancePayload):
                raise ValueError("fragrance is required for create")
            result = client.create_fragrance(fragrance)
        else:
            if fragrance is None or not isinstance(fragrance, UpdateFragrancePayload):
                raise ValueError("update requires a fragrance payload with id")
            result = client.update_fragrance(fragrance)
        LOGGER.info("TOOL_RESULT operation=%s method=%s path=/api/v1/fragrances resource_id=%s", operation, _method(operation), result.id)
        return result.model_dump(mode="json")

    return fragrance_api


def _method(operation: str) -> str:
    return {"create": "POST", "get": "GET", "update": "PUT"}[operation]
