#!/usr/bin/env python3

import argparse
import json
import sys
import uuid
from datetime import datetime, timezone
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent / 'generated'))

from google.protobuf.timestamp_pb2 import Timestamp

from fragrance import fragrance_event_pb2
from fragrance import fragrance_event_type_pb2
from fragrance import fragrance_event_payload_pb2
from fragrance import fragrance_availability_status_pb2
from fragrance import fragrance_gender_pb2
from fragrance import fragrance_longevity_pb2
from fragrance import fragrance_season_pb2
from fragrance import fragrance_sillage_pb2
from fragrance import fragrance_type_pb2

from kafka import KafkaProducer


def load_fragrance_data(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        return json.load(f)


def timestamp_from_millis(millis):
    """Конвертирует миллисекунды epoch в google.protobuf.Timestamp."""
    ts = Timestamp()
    ts.seconds = millis // 1000
    ts.nanos = (millis % 1000) * 1_000_000
    return ts


def timestamp_from_iso(iso_string):
    """Конвертирует ISO 8601 строку в google.protobuf.Timestamp."""
    ts = Timestamp()
    dt = datetime.fromisoformat(iso_string.replace('Z', '+00:00'))
    ts.FromDatetime(dt)
    return ts


def create_fragrance_event(data_dict):
    """Создаёт FragranceEvent из словаря (загруженного из JSON)."""

    # Event type
    event_type_str = data_dict.get('event_type', 'FRAGRANCE_EVENT_TYPE_CREATED')
    event_type = fragrance_event_type_pb2.FragranceEventType.Value(event_type_str)

    # Event timestamp
    event_timestamp_raw = data_dict.get('event_timestamp')
    if isinstance(event_timestamp_raw, (int, float)):
        event_timestamp = timestamp_from_millis(int(event_timestamp_raw))
    elif isinstance(event_timestamp_raw, str):
        event_timestamp = timestamp_from_iso(event_timestamp_raw)
    else:
        # Текущее время по умолчанию
        event_timestamp = Timestamp()
        event_timestamp.FromDatetime(datetime.now(timezone.utc))

    # Payload
    payload_dict = data_dict.get('payload', {})

    # created_at
    created_at = None
    if payload_dict.get('created_at'):
        created_at = timestamp_from_iso(payload_dict['created_at'])

    # updated_at
    updated_at = None
    if payload_dict.get('updated_at'):
        updated_at = timestamp_from_iso(payload_dict['updated_at'])

    # Конвертация repeated enum полей (в JSON они как int)
    types_raw = payload_dict.get('type', payload_dict.get('types', []))
    seasons_raw = payload_dict.get('season', payload_dict.get('seasons', []))

    payload = fragrance_event_payload_pb2.FragranceEventPayload(
        id=payload_dict.get('id', ''),
        name=payload_dict.get('name', ''),
        resume=payload_dict.get('resume', ''),
        rating=payload_dict.get('rating', 0),
        concentration=payload_dict.get('concentration', 0),
        type=types_raw,
        gender=payload_dict.get('gender', 0),
        season=seasons_raw,
        longevity=payload_dict.get('longevity', 0),
        sillage=payload_dict.get('sillage', 0),
        availability=payload_dict.get('availability', 0),
    )

    if created_at:
        payload.created_at.CopyFrom(created_at)
    if updated_at:
        payload.updated_at.CopyFrom(updated_at)

    # Собираем FragranceEvent
    event_uuid = uuid.uuid4()
    event_uuid_str = str(event_uuid)
    fragrance_event = fragrance_event_pb2.FragranceEvent(
        event_id=event_uuid_str,
        event_type=event_type,
    )
    fragrance_event.event_timestamp.CopyFrom(event_timestamp)
    fragrance_event.payload.CopyFrom(payload)

    return fragrance_event


def send_event(bootstrap_servers, topic, event):
    producer = KafkaProducer(
        bootstrap_servers=bootstrap_servers,
        value_serializer=lambda v: v.SerializeToString()
    )

    try:
        key = event.payload.id if event.payload.id else event.event_id
        future = producer.send(topic, key=key.encode('utf-8'), value=event)
        record_metadata = future.get(timeout=10)

        print(f"Event sent successfully!")
        print(f"Topic: {record_metadata.topic}")
        print(f"Partition: {record_metadata.partition}")
        print(f"Offset: {record_metadata.offset}")

        return True
    except Exception as e:
        print(f"Error sending event: {e}", file=sys.stderr)
        return False
    finally:
        producer.flush()
        producer.close()


def main():
    parser = argparse.ArgumentParser(description='Send Fragrance event to Kafka')
    parser.add_argument(
        '--key',
        help='Kafka message key (default: payload.id from file)'
    )
    parser.add_argument(
        '--bootstrap-servers',
        default='localhost:9092',
        help='Kafka bootstrap servers (default: localhost:9092)'
    )
    parser.add_argument(
        '--topic',
        default='outer-fragrance-events',
        help='Kafka topic (default: outer-fragrance-events)'
    )
    parser.add_argument(
        '--file',
        default='examples/fragrance_created_example_1.json',
        help='Path to JSON file with Fragrance data (default: examples/fragrance_created_example_1.json)'
    )
    parser.add_argument(
        '--event-id',
        dest='event_id',
        help='Event ID (overrides value from file)'
    )
    parser.add_argument(
        '--payload-id',
        dest='payload_id',
        help='Fragrance ID in payload (overrides value from file)'
    )
    parser.add_argument(
        '--event-type',
        dest='event_type',
        choices=[
            'FRAGRANCE_EVENT_TYPE_CREATED',
            'FRAGRANCE_EVENT_TYPE_UPDATED',
            'FRAGRANCE_EVENT_TYPE_DELETED'
        ],
        help='Event type (overrides value from file)'
    )
    parser.add_argument(
        '--payload-updated-at',
        dest='payload_updated_at',
        help='Updated at timestamp in ISO format, e.g. 2026-01-22T12:00:00Z (overrides value from file)'
    )

    args = parser.parse_args()

    script_dir = Path(__file__).parent
    file_path = script_dir / args.file

    if not file_path.exists():
        print(f"Error: File not found: {file_path}", file=sys.stderr)
        return 1

    fragrance_data = load_fragrance_data(file_path)

    # Применяем переопределения из CLI-аргументов
    if args.event_id:
        fragrance_data['event_id'] = args.event_id
    if args.event_type:
        fragrance_data['event_type'] = args.event_type
    if args.payload_id:
        if 'payload' not in fragrance_data:
            fragrance_data['payload'] = {}
        fragrance_data['payload']['id'] = args.payload_id
    if args.payload_updated_at:
        if 'payload' not in fragrance_data:
            fragrance_data['payload'] = {}
        fragrance_data['payload']['updated_at'] = args.payload_updated_at

    print("Sending Fragrance event:")
    print(json.dumps(fragrance_data, indent=2, ensure_ascii=False))

    event = create_fragrance_event(fragrance_data)

    success = send_event(args.bootstrap_servers, args.topic, event)

    return 0 if success else 1


if __name__ == '__main__':
    sys.exit(main())