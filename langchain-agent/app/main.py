from __future__ import annotations

from contextlib import asynccontextmanager

import httpx
from fastapi import FastAPI

from app.agent.factory import create_intent_agent
from app.agent.response import AgentResponse
from app.agent.service import CommandService
from app.agent.tools import create_fragrance_api_tool
from app.api.models import CommandRequest
from app.config import Settings
from app.my_scents.client import MyScentsClient


def create_command_service() -> CommandService:
    settings = Settings()
    timeout = httpx.Timeout(settings.http_read_timeout_seconds, connect=settings.http_connect_timeout_seconds)
    client = MyScentsClient(settings.my_scents_base_url, timeout)
    tool = create_fragrance_api_tool(client, requires_authorization=True)
    return CommandService(create_intent_agent(settings, tool), client, tool)


def create_app(service: CommandService | None = None) -> FastAPI:
    service = service or create_command_service()

    @asynccontextmanager
    async def lifespan(_: FastAPI):
        try:
            yield
        finally:
            service.close()

    app = FastAPI(title="my-scents LangChain agent", docs_url=None, redoc_url=None, lifespan=lifespan)

    @app.get("/health")
    def health() -> dict[str, str]:
        return {"status": "ok"}

    @app.post("/v1/agent/commands", response_model=AgentResponse)
    def command(request: CommandRequest) -> AgentResponse:
        return service.execute(request.message, request.confirmed, request.prepared_operation_id)

    return app


app = create_app()
