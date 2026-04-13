#!/bin/bash
# QVC Survivors launcher for macOS / Linux
# Requires Java 17+ installed
DIR="$(cd "$(dirname "$0")" && pwd)"
JAR=$(ls "$DIR"/QVCSurvivors-*.jar 2>/dev/null | head -1)
if [ -z "$JAR" ]; then
    echo "Error: QVCSurvivors JAR not found in $DIR"
    exit 1
fi
java -cp "$JAR" com.qvc.survivors.Launcher "$@"
