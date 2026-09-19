from pathlib import Path


def load_system_prompt() -> str:
    """Load trusted instructions without interpolating untrusted user input."""
    return (Path(__file__).resolve().parents[2] / "prompts" / "system.md").read_text(encoding="utf-8")
