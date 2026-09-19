import httpx
import pytest

from app.agent.tools import create_fragrance_api_tool
from app.my_scents.client import MyScentsClient


PAYLOAD = {
    "name": "Example", "rating": 8, "resume": "Example description", "concentration": "EAU_DE_PARFUM",
    "type": ["WOODY"], "gender": "UNISEX", "season": ["WINTER"], "longevity": "STRONG",
    "sillage": "MODERATE", "availability": "AVAILABLE",
}
IDENTIFIER = "123e4567-e89b-42d3-a456-426614174000"


@pytest.mark.parametrize("operation,expected_method", [("create", "POST"), ("get", "GET"), ("update", "PUT")])
def test_tool_only_calls_allowed_public_endpoint(operation, expected_method):
    def handler(request: httpx.Request) -> httpx.Response:
        assert request.method == expected_method
        assert request.url.path == "/api/v1/fragrances"
        return httpx.Response(200, json={"id": IDENTIFIER, **PAYLOAD})

    tool = create_fragrance_api_tool(MyScentsClient("http://test", httpx.Timeout(5), httpx.MockTransport(handler)))
    arguments = {"operation": operation}
    if operation == "get":
        arguments["fragrance_id"] = IDENTIFIER
    else:
        arguments["fragrance"] = {**PAYLOAD, **({"id": IDENTIFIER} if operation == "update" else {})}
    assert tool.invoke(arguments)["id"] == IDENTIFIER


def test_tool_rejects_unallowlisted_operation():
    tool = create_fragrance_api_tool(MyScentsClient("http://test", httpx.Timeout(5), httpx.MockTransport(lambda request: httpx.Response(200))))
    with pytest.raises(Exception):
        tool.invoke({"operation": "delete"})


def test_create_rejects_payload_with_id_without_http_call():
    tool = create_fragrance_api_tool(
        MyScentsClient(
            "http://test",
            httpx.Timeout(5),
            httpx.MockTransport(lambda request: (_ for _ in ()).throw(AssertionError("unexpected HTTP call"))),
        )
    )
    with pytest.raises(Exception):
        tool.invoke({"operation": "create", "fragrance": {**PAYLOAD, "id": IDENTIFIER}})


def test_tool_writes_only_safe_call_metadata_to_console(capsys):
    tool = create_fragrance_api_tool(
        MyScentsClient(
            "http://credentials-are-not-logged:test@example.test",
            httpx.Timeout(5),
            httpx.MockTransport(lambda request: httpx.Response(200, json={"id": IDENTIFIER, **PAYLOAD})),
        )
    )

    tool.invoke({"operation": "create", "fragrance": PAYLOAD})

    output = capsys.readouterr().err
    assert output.splitlines() == [
        f"TOOL_CALL operation=create method=POST path=/api/v1/fragrances resource_id=",
        f"TOOL_RESULT operation=create method=POST path=/api/v1/fragrances resource_id={IDENTIFIER}",
    ]
    assert PAYLOAD["resume"] not in output
    assert "credentials-are-not-logged" not in output
