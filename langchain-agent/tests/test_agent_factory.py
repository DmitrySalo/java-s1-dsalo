import httpx
from unittest.mock import patch

from app.agent.factory import create_intent_agent
from app.agent.intent import Intent
from app.agent.prompts import load_system_prompt
from app.agent.tools import create_fragrance_api_tool
from app.config import Settings
from app.my_scents.client import MyScentsClient
from langchain.agents.structured_output import ProviderStrategy


def test_intent_agent_uses_ollama_json_schema_response_format():
    client = MyScentsClient("http://test", timeout=httpx.Timeout(5))
    try:
        with patch("app.agent.factory.ChatOllama", return_value=object()), patch(
            "app.agent.factory.create_agent", return_value=object()
        ) as create_agent:
            create_intent_agent(Settings(), create_fragrance_api_tool(client))

        response_format = create_agent.call_args.kwargs["response_format"]
        assert isinstance(response_format, ProviderStrategy)
        assert response_format.schema is Intent
        assert create_agent.call_args.kwargs["system_prompt"] == load_system_prompt()
        assert len(create_agent.call_args.kwargs["tools"]) == 1
    finally:
        client.close()
