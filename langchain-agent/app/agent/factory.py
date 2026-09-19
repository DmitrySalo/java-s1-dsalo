from __future__ import annotations

from typing import Protocol

from langchain.agents import create_agent
from langchain.agents.structured_output import ProviderStrategy
from langchain_core.tools import BaseTool
from langchain_ollama import ChatOllama

from app.agent.intent import Intent
from app.agent.prompts import load_system_prompt
from app.config import Settings


class AgentInvoker(Protocol):
    def invoke(self, input: dict[str, object], config: dict[str, int]) -> dict[str, object]: ...


def create_intent_agent(settings: Settings, fragrance_api_tool: BaseTool) -> AgentInvoker:
    """Build the bounded LangChain agent with its sole allowlisted API tool."""
    model = ChatOllama(
        model=settings.ollama_model,
        base_url=settings.ollama_base_url,
        temperature=0,
        num_predict=512,
        client_kwargs={"timeout": settings.ollama_timeout_seconds},
    )
    return create_agent(
        model=model,
        tools=[fragrance_api_tool],
        system_prompt=load_system_prompt(),
        # Ollama supports constrained JSON Schema output; avoid the unreliable tool-calling fallback.
        response_format=ProviderStrategy(Intent),
    )
