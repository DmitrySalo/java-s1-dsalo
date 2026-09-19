from __future__ import annotations

from typing import Literal
from uuid import UUID

from pydantic import BaseModel, ConfigDict, field_validator

from app.my_scents.models import CreateFragrancePayload


class Intent(BaseModel):
    """Strict structured output returned by the LLM before policy execution."""

    model_config = ConfigDict(extra="forbid")

    action: Literal["create", "get", "update", "help"]
    fragrance_id: UUID | None = None
    fragrance: CreateFragrancePayload | None = None

    @field_validator("fragrance_id", mode="before")
    @classmethod
    def normalize_null_identifier(cls, value: object) -> object:
        if value == "null":
            return None
        return value
