#!/bin/bash
# QVC Survivors launcher for macOS / Linux
# Requires Java 17+ installed
DIR="$(cd "$(dirname "$0")" && pwd)"
java -cp "$DIR/QVCSurvivors-2.3.0.jar" com.qvc.survivors.Launcher "$@"
