class MyScentsError(Exception):
    """Safe base error for my-scents integration failures."""


class MyScentsNetworkError(MyScentsError):
    """The upstream service could not be contacted."""


class MyScentsApiError(MyScentsError):
    def __init__(self, status_code: int) -> None:
        self.status_code = status_code
        super().__init__(f"my-scents returned HTTP {status_code}")
