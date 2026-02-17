#!/usr/bin/env bash
set -euo pipefail

cd Travelio
./gradlew clean bootJar -x test --no-daemon
