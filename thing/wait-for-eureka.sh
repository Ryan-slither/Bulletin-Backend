#!/bin/sh

HOST="eureka"
PORT=8761

echo "Waiting for Eureka Server at $HOST:$PORT..."

while ! nc -z "$HOST" "$PORT"; do
  echo "Eureka not available yet - sleeping"
  sleep 5
done

echo "Eureka is up - starting application"
exec ./gradlew bootRun
