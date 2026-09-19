import json
from uuid import UUID

import httpx
import pytest

from app.my_scents.client import MyScentsClient
from app.my_scents.errors import MyScentsApiError, MyScentsNetworkError
from app.my_scents.models import CreateFragrancePayload


PAYLOAD = {
    "name": "Example",
    "rating": 8,
    "resume": "Example description",
    "concentration": "EAU_DE_PARFUM",
    "type": ["WOODY"],
    "gender": "UNISEX",
    "season": ["WINTER"],
    "longevity": "STRONG",
    "sillage": "MODERATE",
    "availability": "AVAILABLE",
}
IDENTIFIER = "123e4567-e89b-42d3-a456-426614174000"


def client(handler):
    return MyScentsClient(
        "http://my-scents.test",
        httpx.Timeout(5, connect=2),
        httpx.MockTransport(handler),
    )


def test_create_uses_allowlisted_path_and_json_body():
    def handler(request: httpx.Request) -> httpx.Response:
        assert request.method == "POST"
        assert request.url.path == "/api/v1/fragrances"
        assert json.loads(request.content) == PAYLOAD
        return httpx.Response(200, json={"id": IDENTIFIER, **PAYLOAD})

    result = client(handler).create_fragrance(CreateFragrancePayload.model_validate(PAYLOAD))
    assert result.id == UUID(IDENTIFIER)


def test_get_sends_only_fragrance_id_query_parameter():
    def handler(request: httpx.Request) -> httpx.Response:
        assert request.method == "GET"
        assert request.url.path == "/api/v1/fragrances"
        assert dict(request.url.params) == {"fragranceId": IDENTIFIER}
        return httpx.Response(200, json={"id": IDENTIFIER, **PAYLOAD})

    assert client(handler).get_fragrance(IDENTIFIER).name == "Example"


def test_api_error_is_safe():
    with pytest.raises(MyScentsApiError) as error:
        client(lambda request: httpx.Response(503)).get_fragrance(IDENTIFIER)
    assert error.value.status_code == 503


def test_network_error_is_safe():
    def handler(request: httpx.Request) -> httpx.Response:
        raise httpx.ConnectError("not available", request=request)

    with pytest.raises(MyScentsNetworkError):
        client(handler).get_fragrance(IDENTIFIER)


def test_payload_normalizes_bracketed_singleton_enum_sets_from_local_model():
    payload = CreateFragrancePayload.model_validate({**PAYLOAD, "type": "[WOODY]", "season": "[WINTER]"})
    assert payload.type == {"WOODY"}
    assert payload.season == {"WINTER"}
