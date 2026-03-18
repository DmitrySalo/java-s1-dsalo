#!/bin/bash

set -e

SCHEMA_REGISTRY_URL="${SCHEMA_REGISTRY_URL:-http://schema-registry:8081}"
PROTO_DIR="${PROTO_DIR:-./my-scents/src/main/proto/fragrance}"

echo "Waiting for Schema Registry to be ready..."
MAX_RETRIES=30
RETRY_COUNT=0

until curl -s -f "${SCHEMA_REGISTRY_URL}/subjects" > /dev/null 2>&1; do
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -ge $MAX_RETRIES ]; then
        echo "ERROR: Schema Registry did not become ready after ${MAX_RETRIES} attempts"
        exit 1
    fi
    echo "Waiting for Schema Registry... attempt ${RETRY_COUNT}/${MAX_RETRIES}"
    sleep 2
done

echo "Schema Registry is ready!"

# Function to register a Protobuf schema
register_schema() {
    local subject=$1
    local proto_file=$2
    local references="${3:-[]}"
    local schema_content

    echo "Registering schema for subject: ${subject}"

    # Read proto file content and escape for JSON
    schema_content=$(cat "${PROTO_DIR}/${proto_file}" | jq -Rs .)

    # Create JSON payload
    local payload=$(jq -n \
        --argjson schema "$schema_content" \
        --argjson refs "$references" \
        '{
            schemaType: "PROTOBUF",
            schema: $schema,
            references: $refs
        }')

    # Register schema
    response=$(curl -s -w "\n%{http_code}" -X POST \
        -H "Content-Type: application/vnd.schemaregistry.v1+json" \
        --data "${payload}" \
        "${SCHEMA_REGISTRY_URL}/subjects/${subject}/versions")

    http_code=$(echo "$response" | tail -n 1)
    response_body=$(echo "$response" | sed '$d')

    if [ "$http_code" -eq 200 ] || [ "$http_code" -eq 201 ]; then
        echo "✓ Successfully registered schema for subject: ${subject}"
        echo "  Response: ${response_body}"
    else
        echo "✗ Failed to register schema for subject: ${subject}"
        echo "  HTTP Code: ${http_code}"
        echo "  Response: ${response_body}"
        return 1
    fi
}

register_schema_with_refs() {
    local subject=$1
    local proto_file=$2
    local references=$3

    register_schema "$subject" "$proto_file" "$references"
}

echo ""
echo "=========================================="
echo "Registering Fragrance Event Schemas"
echo "=========================================="

register_schema "fragrance-availability-status-value" "fragrance_availability_status.proto"
register_schema "fragrance-concentration-value" "fragrance_concentration.proto"
register_schema "fragrance-gender-value" "fragrance_gender.proto"
register_schema "fragrance-longevity-value" "fragrance_longevity.proto"
register_schema "fragrance-season-value" "fragrance_season.proto"
register_schema "fragrance-sillage-value" "fragrance_sillage.proto"
register_schema "fragrance-type-value" "fragrance_type.proto"

register_schema "fragrance-event-type-value" "fragrance_event_type.proto"

register_schema_with_refs "fragrance-event-payload-value" "fragrance_event_payload.proto" \
  '[
    {"name":"fragrance/fragrance_availability_status.proto","subject":"fragrance-availability-status-value","version":1},
    {"name":"fragrance/fragrance_concentration.proto","subject":"fragrance-concentration-value","version":1},
    {"name":"fragrance/fragrance_gender.proto","subject":"fragrance-gender-value","version":1},
    {"name":"fragrance/fragrance_longevity.proto","subject":"fragrance-longevity-value","version":1},
    {"name":"fragrance/fragrance_season.proto","subject":"fragrance-season-value","version":1},
    {"name":"fragrance/fragrance_sillage.proto","subject":"fragrance-sillage-value","version":1},
    {"name":"fragrance/fragrance_type.proto","subject":"fragrance-type-value","version":1}
  ]'

# Register fragrance_event.proto for my_scents_fragrance_event topic
register_schema_with_refs "my-scents-fragrance-event-value" "fragrance_event.proto" \
  '[
    {"name":"fragrance/fragrance_availability_status.proto","subject":"fragrance-availability-status-value","version":1},
    {"name":"fragrance/fragrance_concentration.proto","subject":"fragrance-concentration-value","version":1},
    {"name":"fragrance/fragrance_gender.proto","subject":"fragrance-gender-value","version":1},
    {"name":"fragrance/fragrance_longevity.proto","subject":"fragrance-longevity-value","version":1},
    {"name":"fragrance/fragrance_season.proto","subject":"fragrance-season-value","version":1},
    {"name":"fragrance/fragrance_sillage.proto","subject":"fragrance-sillage-value","version":1},
    {"name":"fragrance/fragrance_type.proto","subject":"fragrance-type-value","version":1},
    {"name":"fragrance/fragrance_event_type.proto","subject":"fragrance-event-type-value","version":1},
    {"name":"fragrance/fragrance_event_payload.proto","subject":"fragrance-event-payload-value","version":1}
  ]'

# Register fragrance_event.proto for my_scents_fragrance_event_qlt topic
register_schema_with_refs "my-scents-fragrance-event-qlt-value" "fragrance_event.proto" \
  '[
    {"name":"fragrance/fragrance_availability_status.proto","subject":"fragrance-availability-status-value","version":1},
    {"name":"fragrance/fragrance_concentration.proto","subject":"fragrance-concentration-value","version":1},
    {"name":"fragrance/fragrance_gender.proto","subject":"fragrance-gender-value","version":1},
    {"name":"fragrance/fragrance_longevity.proto","subject":"fragrance-longevity-value","version":1},
    {"name":"fragrance/fragrance_season.proto","subject":"fragrance-season-value","version":1},
    {"name":"fragrance/fragrance_sillage.proto","subject":"fragrance-sillage-value","version":1},
    {"name":"fragrance/fragrance_type.proto","subject":"fragrance-type-value","version":1},
    {"name":"fragrance/fragrance_event_type.proto","subject":"fragrance-event-type-value","version":1},
    {"name":"fragrance/fragrance_event_payload.proto","subject":"fragrance-event-payload-value","version":1}
  ]'

# Register fragrance_event.proto for outer_fragrance_event topic
register_schema_with_refs "outer-fragrance-event-value" "fragrance_event.proto" \
  '[
    {"name":"fragrance/fragrance_availability_status.proto","subject":"fragrance-availability-status-value","version":1},
    {"name":"fragrance/fragrance_concentration.proto","subject":"fragrance-concentration-value","version":1},
    {"name":"fragrance/fragrance_gender.proto","subject":"fragrance-gender-value","version":1},
    {"name":"fragrance/fragrance_longevity.proto","subject":"fragrance-longevity-value","version":1},
    {"name":"fragrance/fragrance_season.proto","subject":"fragrance-season-value","version":1},
    {"name":"fragrance/fragrance_sillage.proto","subject":"fragrance-sillage-value","version":1},
    {"name":"fragrance/fragrance_type.proto","subject":"fragrance-type-value","version":1},
    {"name":"fragrance/fragrance_event_type.proto","subject":"fragrance-event-type-value","version":1},
    {"name":"fragrance/fragrance_event_payload.proto","subject":"fragrance-event-payload-value","version":1}
  ]'

# Register fragrance_event.proto for outer_fragrance_event_qlt topic
register_schema_with_refs "outer-fragrance-event-qlt-value" "fragrance_event.proto" \
  '[
    {"name":"fragrance/fragrance_availability_status.proto","subject":"fragrance-availability-status-value","version":1},
    {"name":"fragrance/fragrance_concentration.proto","subject":"fragrance-concentration-value","version":1},
    {"name":"fragrance/fragrance_gender.proto","subject":"fragrance-gender-value","version":1},
    {"name":"fragrance/fragrance_longevity.proto","subject":"fragrance-longevity-value","version":1},
    {"name":"fragrance/fragrance_season.proto","subject":"fragrance-season-value","version":1},
    {"name":"fragrance/fragrance_sillage.proto","subject":"fragrance-sillage-value","version":1},
    {"name":"fragrance/fragrance_type.proto","subject":"fragrance-type-value","version":1},
    {"name":"fragrance/fragrance_event_type.proto","subject":"fragrance-event-type-value","version":1},
    {"name":"fragrance/fragrance_event_payload.proto","subject":"fragrance-event-payload-value","version":1}
  ]'

echo ""
echo "=========================================="
echo "Schema Registration Complete!"
echo "=========================================="

# List all registered subjects
echo ""
echo "Registered subjects:"
curl -s "${SCHEMA_REGISTRY_URL}/subjects" | jq .

echo ""
echo "Schema Registry is ready at: ${SCHEMA_REGISTRY_URL}"
echo "You can now view schemas in Kafka UI at: http://localhost:8090"
