from __future__ import annotations

from enum import StrEnum
from typing import Literal

from pydantic import BaseModel, ConfigDict, Field


class Status(StrEnum):
    SUCCESS = "success"
    CONFIRMATION_REQUIRED = "confirmation_required"
    ERROR = "error"


class AgentResponse(BaseModel):
    """The shared normalized response contract for HTTP and CLI callers."""

    model_config = ConfigDict(extra="forbid")

    status: Status
    action: Literal["create", "get", "update", "help"]
    data: dict[str, object] | None = None
    errors: list[str] = Field(default_factory=list)


def error_response(action: Literal["create", "get", "update", "help"], message: str) -> AgentResponse:
    return AgentResponse(status=Status.ERROR, action=action, errors=[message])
