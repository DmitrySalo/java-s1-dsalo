import httpx
from fastapi.testclient import TestClient

from app.agent.service import CommandService
from app.main import create_app
from app.my_scents.client import MyScentsClient

class HelpAgent:
    def invoke(self, input: dict[str, object], config: dict[str, int]) -> dict[str, object]:
        return {"structured_response": {"action": "help", "fragrance_id": None, "fragrance": None}}


def test_health_does_not_probe_my_scents():
    client = TestClient(create_app())
    assert client.get("/health").json() == {"status": "ok"}


def test_command_endpoint_returns_normalized_contract():
    transport = httpx.MockTransport(lambda request: (_ for _ in ()).throw(AssertionError("no upstream call")))
    service = CommandService(HelpAgent(), MyScentsClient("http://test", httpx.Timeout(5), transport))
    response = TestClient(create_app(service)).post("/v1/agent/commands", json={"message": "help"})
    assert response.status_code == 200
    assert response.json() == {
        "status": "success", "action": "help",
        "data": {"operations": ["create", "get", "update"], "confirmation_required_for": ["create", "update"]},
        "errors": [],
    }


def test_command_requires_json_boolean_confirmation():
    response = TestClient(create_app()).post(
        "/v1/agent/commands", json={"message": "help", "confirmed": "true"}
    )
    assert response.status_code == 422
