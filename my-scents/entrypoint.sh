#!/bin/sh
set -e

# Use exec to ensure proper signal handling
# This makes java PID 1, so SIGTERM goes directly to the JVM
exec java $JAVA_OPTS -jar /app/app.jar "$@"
