#!/bin/bash
# Build fix script for Project-Anti

echo "=== Project-Anti Build Fix Script ==="

# Check if we're in the right directory
if [ ! -f "gradlew" ]; then
    echo "Error: Not in project root directory"
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

# Set environment variables for stable build
export GRADLE_OPTS="-Xmx4g -XX:+UseG1GC -Dfile.encoding=UTF-8"
export JAVA_OPTS="-Xmx2g"

# Try to find Java installation
if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
elif [ -d "/usr/lib/jvm/java-11-openjdk-amd64" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
fi

echo "Using JAVA_HOME: $JAVA_HOME"

# Clean any corrupted Gradle cache
rm -rf ~/.gradle/caches/
rm -rf .gradle/

echo "Cleaned Gradle caches"

# Initialize Gradle wrapper
./gradlew wrapper --gradle-version=8.5

echo "Build environment prepared"
echo "Try running: ./gradlew clean build"
