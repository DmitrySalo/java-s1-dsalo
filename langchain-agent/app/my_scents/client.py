from __future__ import annotations

import httpx

from app.my_scents.errors import MyScentsApiError, MyScentsNetworkError
from app.my_scents.models import CreateFragrancePayload, FragranceResponse, UpdateFragrancePayload

_FRAGRANCES_PATH = "/api/v1/fragrances"


class MyScentsClient:
    """Client constrained to the three supported public fragrance operations."""

    def __init__(
        self,
        base_url: str,
        timeout: httpx.Timeout,
        transport: httpx.BaseTransport | None = None,
    ) -> None:
        self._client = httpx.Client(base_url=base_url, timeout=timeout, transport=transport)

    def close(self) -> None:
        self._client.close()

    def create_fragrance(self, payload: CreateFragrancePayload) -> FragranceResponse:
        return self._request("POST", json=payload.model_dump(mode="json"))

    def get_fragrance(self, fragrance_id: str) -> FragranceResponse:
        return self._request("GET", params={"fragranceId": fragrance_id})

    def update_fragrance(self, payload: UpdateFragrancePayload) -> FragranceResponse:
        return self._request("PUT", json=payload.model_dump(mode="json"))

    def _request(self, method: str, **kwargs: object) -> FragranceResponse:
        try:
            response = self._client.request(method, _FRAGRANCES_PATH, **kwargs)
        except httpx.RequestError as error:
            raise MyScentsNetworkError("my-scents is unavailable") from error
        if response.is_error:
            raise MyScentsApiError(response.status_code)
        try:
            return FragranceResponse.model_validate(response.json())
        except (ValueError, TypeError) as error:
            raise MyScentsNetworkError("my-scents returned an invalid response") from error
