#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/../.."
PROTO_DIR="$PROJECT_ROOT/my-scents/src/main/proto"
OUTPUT_DIR="$SCRIPT_DIR/generated"

echo "Compiling proto files..."
echo "Proto directory: $PROTO_DIR"
echo "Output directory: $OUTPUT_DIR"

mkdir -p "$OUTPUT_DIR"

# Компилируем с grpc_python_out для генерации _pb2_grpc.py файлов
python -m grpc_tools.protoc \
  --proto_path="$PROTO_DIR" \
  --python_out="$OUTPUT_DIR" \
  --grpc_python_out="$OUTPUT_DIR" \
  $(find "$PROTO_DIR" -name "*.proto")

if [ $? -eq 0 ]; then
    echo "Proto files compiled successfully!"
    echo "Generated files are in: $OUTPUT_DIR"
    touch "$OUTPUT_DIR/__init__.py"
else
    echo "Error: Failed to compile proto files"
    echo "Make sure grpcio-tools is installed: pip install grpcio-tools"
    exit 1
fi