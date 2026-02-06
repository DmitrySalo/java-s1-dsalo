# Stage 1: Build
FROM gradle:9.3.0-jdk25 AS builder

# Set working directory
WORKDIR /build

# Copy gradle wrapper and build files first for better caching
COPY my-scents/gradlew settings.gradle ./
COPY my-scents/gradle gradle/
COPY my-scents/build.gradle ./my-scents/

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies with BuildKit cache mount
RUN --mount=type=cache,target=/root/.gradle ./gradlew dependencies --no-daemon

# Copy source code
COPY my-scents/src ./my-scents/src/

# Build the application
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :my-scents:clean :my-scents:bootJar -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:25-jre

# Set working directory
WORKDIR /app

# Create non-root user and group (используем стандартные команды вместо Alpine-специфичных)
RUN groupadd -g 1001 myscents && \
    useradd -u 1001 -g myscents -s /bin/bash -m myscents

# Copy the JAR from builder stage (adjust path based on your build.gradle)
COPY --from=builder /build/my-scents/build/libs/*.jar ./app.jar

# Copy entrypoint script
COPY my-scents/entrypoint.sh ./entrypoint.sh

# Make entrypoint executable and change ownership to non-root user
RUN sed -i 's/\r$//' entrypoint.sh && \
    chmod +x entrypoint.sh && \
    chown -R myscents:myscents /app

# Switch to non-root user
USER myscents

# Expose ports
EXPOSE 8080
EXPOSE 8081
EXPOSE 50053
EXPOSE 9010

# Health check using actuator endpoint (curl вместо wget)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/manage/health || exit 1

# Environment variables for JVM tuning with profiling support
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+ExitOnOutOfMemoryError \
    -Dcom.sun.management.jmxremote=true \
    -Dcom.sun.management.jmxremote.port=9010 \
    -Dcom.sun.management.jmxremote.rmi.port=9010 \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Djava.rmi.server.hostname=localhost"

# Use entrypoint script for proper signal handling
ENTRYPOINT ["/app/entrypoint.sh"]