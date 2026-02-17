#!/usr/bin/env bash
set -euo pipefail

cd Travelio

JAR_PATH="build/libs/Travelio-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_PATH" ]; then
  ./gradlew bootJar -x test --no-daemon
fi

exec java -Dserver.port="${PORT:-8080}" -jar "$JAR_PATH"
