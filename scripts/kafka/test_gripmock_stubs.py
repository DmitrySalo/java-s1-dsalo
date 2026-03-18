#!/usr/bin/env python3

import sys
import grpc
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent / 'generated'))

from fragrance.fragrance_service_pb2 import GetFragranceRequest
from fragrance.fragrance_service_pb2_grpc import FragranceServiceStub


def test_fragrance_stubs():
    print("Testing Fragrance GripMock stubs...")
    print("-" * 50)

    channel = grpc.insecure_channel('localhost:50051')
    stub = FragranceServiceStub(channel)

    test_ids = [
        ("fc044a50-339e-4683-977a-495cbe6f2cfe", "Kafka Created Event Fragrance"),
        ("2b8898a6-543d-4628-b6b7-5df892f1b3ec", "Kafka Created Event Fragrance"),
        ("2b8898a6-543d-4628-b6b7-5df892f1b3ec", "Kafka Updated Event Fragrance"),
    ]

    for fragrance_id, description in test_ids:
        try:
            request = GetFragranceRequest(fragrance_id=fragrance_id)
            response = stub.GetFragrance(request)

            if response.HasField('data'):
                print(f" {description}")
                print(f"  ID: {response.data.id}")
                print(f"  Name: {response.data.name}")
                print(f"  Resume: {response.data.resume}")
                print(f"  Concentration: {response.data.concentration}")
                print(f"  Rating: {response.data.rating}")
                print(f"  Type: {list(response.data.type)}")
                print(f"  Gender: {response.data.gender}")
                print(f"  Season: {list(response.data.season)}")
                print(f"  Longevity: {response.data.longevity}")
                print(f"  Sillage: {response.data.sillage}")
                print(f"  Availability: {response.data.availability}")
                print(f"  Created_at: {response.data.created_at}")
                print(f"  Updated_at: {response.data.updated_at}")
            elif response.HasField('error'):
                print(f" {description} - Error: {response.error.message}")
            print()
        except grpc.RpcError as e:
            print(f" {description} - gRPC Error: {e.code()} - {e.details()}")
            print()

    channel.close()


def main():
    print("=" * 50)
    print("GripMock Stubs Test")
    print("=" * 50)
    print()

    test_fragrance_stubs()

    print("=" * 50)
    print("Test completed!")
    print("=" * 50)


if __name__ == '__main__':
    main()