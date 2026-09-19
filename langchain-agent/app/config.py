from __future__ import annotations

from urllib.parse import urlparse

from pydantic import Field, field_validator
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Runtime configuration supplied through environment variables or .env."""

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    my_scents_base_url: str = Field(default="http://localhost:8087")
    ollama_base_url: str = Field(default="http://localhost:11434")
    ollama_model: str = Field(default="llama3.2", min_length=1, max_length=128)
    http_connect_timeout_seconds: float = Field(default=3.0, gt=0, le=30)
    http_read_timeout_seconds: float = Field(default=10.0, gt=0, le=60)
    ollama_timeout_seconds: float = Field(default=30.0, gt=0, le=120)

    @field_validator("my_scents_base_url", "ollama_base_url")
    @classmethod
    def validate_base_url(cls, value: str) -> str:
        parsed = urlparse(value)
        if parsed.scheme not in {"http", "https"} or not parsed.hostname:
            raise ValueError("Base URL must be an absolute HTTP(S) URL")
        if parsed.username or parsed.password or parsed.query or parsed.fragment:
            raise ValueError("Base URL must not contain credentials, query, or fragment")
        return value.rstrip("/")
