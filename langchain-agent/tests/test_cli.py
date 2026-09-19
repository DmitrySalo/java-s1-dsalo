from app.agent.response import AgentResponse, Status
from app import cli


class FakeService:
    def execute(self, message: str, confirmed: bool = False, prepared_operation_id=None) -> AgentResponse:
        assert message == "help"
        assert not confirmed
        return AgentResponse(status=Status.SUCCESS, action="help")

    def close(self) -> None:
        pass


def test_cli_uses_shared_command_service(monkeypatch, capsys):
    monkeypatch.setattr(cli, "create_command_service", lambda: FakeService())
    monkeypatch.setattr("sys.argv", ["app.cli", "help"])
    assert cli.main() == 0
    assert '"status": "success"' in capsys.readouterr().out
