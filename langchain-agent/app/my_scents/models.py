from __future__ import annotations

from enum import StrEnum
from uuid import UUID

from pydantic import BaseModel, ConfigDict, Field, field_validator


class FragranceType(StrEnum):
    ORIENTAL = "ORIENTAL"
    FLORAL = "FLORAL"
    CHYPRE = "CHYPRE"
    FOUGERE = "FOUGERE"
    WOODY = "WOODY"
    AQUATIC = "AQUATIC"
    FRUITY = "FRUITY"
    CITRUS = "CITRUS"
    VANILLA = "VANILLA"
    AMBER = "AMBER"
    LEATHER = "LEATHER"
    ANIMALIC = "ANIMALIC"
    SMOKY = "SMOKY"
    BALSAMIC = "BALSAMIC"
    MUSKY = "MUSKY"
    SPICY = "SPICY"
    ALDEHYDE = "ALDEHYDE"


class Gender(StrEnum):
    MALE = "MALE"
    FEMALE = "FEMALE"
    UNISEX = "UNISEX"


class Season(StrEnum):
    SPRING = "SPRING"
    SUMMER = "SUMMER"
    FALL = "FALL"
    WINTER = "WINTER"
    DAY = "DAY"
    NIGHT = "NIGHT"


class Concentration(StrEnum):
    EAU_DE_COLOGNE = "EAU_DE_COLOGNE"
    EAU_DE_TOILETTE = "EAU_DE_TOILETTE"
    EAU_DE_PARFUM = "EAU_DE_PARFUM"
    PARFUM = "PARFUM"
    EXTRACT_DE_PARFUM = "EXTRACT_DE_PARFUM"


class Longevity(StrEnum):
    VERY_WEAK = "VERY_WEAK"
    WEAK = "WEAK"
    MIDDLE = "MIDDLE"
    STRONG = "STRONG"
    ETERNAL = "ETERNAL"


class Sillage(StrEnum):
    INTIMATE = "INTIMATE"
    MODERATE = "MODERATE"
    STRONG = "STRONG"
    VERY_STRONG = "VERY_STRONG"


class Availability(StrEnum):
    AVAILABLE = "AVAILABLE"
    UNAVAILABLE = "UNAVAILABLE"


class FragrancePayload(BaseModel):
    model_config = ConfigDict(extra="forbid")

    name: str = Field(min_length=1, max_length=256)
    rating: int = Field(ge=0, le=10)
    resume: str = Field(min_length=1, max_length=4000)
    concentration: Concentration
    type: set[FragranceType] = Field(min_length=1)
    gender: Gender
    season: set[Season] = Field(min_length=1)
    longevity: Longevity
    sillage: Sillage
    availability: Availability

    @field_validator("type", "season", mode="before")
    @classmethod
    def normalize_single_enum_set(cls, value: object) -> object:
        """Accept the local model's bracketed singleton form before enum validation."""
        if isinstance(value, str):
            return [value.removeprefix("[").removesuffix("]")]
        return value


class CreateFragrancePayload(FragrancePayload):
    pass


class UpdateFragrancePayload(FragrancePayload):
    id: UUID


class FragranceResponse(FragrancePayload):
    id: UUID
