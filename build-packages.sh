#!/bin/bash
set -e

mvn package -Pcross-platform -q

JAR=$(ls target/QVCSurvivors-*.jar | grep -v original | head -1)
JAR_NAME=$(basename "$JAR")
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

mkdir -p dist

jpackage \
  --type app-image \
  --name "QVC Survivors" \
  --app-version "$VERSION" \
  --vendor "QVC Retail Group" \
  --description "A QVC-themed Vampire Survivors clone" \
  --input target/ \
  --main-jar "$JAR_NAME" \
  --main-class com.qvc.survivors.Launcher \
  --dest dist/ \
  --java-options "-Xmx512m"

echo "Built: dist/QVC Survivors"
