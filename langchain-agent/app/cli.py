from __future__ import annotations

import argparse
import json
from uuid import UUID

from app.agent.response import Status
from app.main import create_command_service


def main() -> int:
    parser = argparse.ArgumentParser(description="Local my-scents agent")
    parser.add_argument("message", help="Natural-language request")
    arguments = parser.parse_args()
    service = create_command_service()
    response = service.execute(arguments.message)
    if response.status == Status.CONFIRMATION_REQUIRED:
        confirmation = input("Подтвердите операцию (да/нет): ").strip().lower()
        if confirmation == "да":
            operation_id = UUID(str(response.data["prepared_operation_id"]))
            response = service.execute(arguments.message, confirmed=True, prepared_operation_id=operation_id)
    try:
        print(json.dumps(response.model_dump(mode="json"), ensure_ascii=False))
        return 0 if response.status != "error" else 1
    finally:
        service.close()


if __name__ == "__main__":
    raise SystemExit(main())
