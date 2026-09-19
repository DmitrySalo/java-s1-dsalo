from uuid import UUID

from pydantic import BaseModel, ConfigDict, Field, StrictBool


class CommandRequest(BaseModel):
    model_config = ConfigDict(extra="forbid")

    message: str = Field(min_length=1, max_length=4000)
    confirmed: StrictBool = False
    prepared_operation_id: UUID | None = None
