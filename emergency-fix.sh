#!/bin/bash
# Emergency Build Recovery Script for Project-Anti

echo "=== Emergency Build Recovery for Project-Anti ==="

# Function to find and set Java home
find_java() {
    local java_paths=(
        "/usr/lib/jvm/java-17-openjdk-amd64"
        "/usr/lib/jvm/java-11-openjdk-amd64"
        "/usr/lib/jvm/java-21-openjdk-amd64"
        "/usr/lib/jvm/default-java"
        "/opt/java/openjdk"
    )

    for path in "${java_paths[@]}"; do
        if [ -d "$path" ] && [ -f "$path/bin/java" ]; then
            export JAVA_HOME="$path"
            export PATH="$JAVA_HOME/bin:$PATH"
            echo "Found and set JAVA_HOME: $JAVA_HOME"
            return 0
        fi
    done

    echo "No valid Java installation found"
    return 1
}

# Set up environment
find_java || exit 1

# Clean everything
echo "Cleaning all Gradle artifacts..."
rm -rf ~/.gradle
rm -rf .gradle
rm -rf build
rm -rf app/build

# Reset environment variables
unset GRADLE_OPTS
unset GRADLE_USER_HOME
unset _JAVA_OPTIONS

export GRADLE_OPTS="-Xmx2048m -Dfile.encoding=UTF-8"
export GRADLE_USER_HOME="$(pwd)/.gradle"

# Make gradlew executable
chmod +x gradlew

echo "Environment reset complete."
echo "JAVA_HOME: $JAVA_HOME"
echo "PATH: $PATH"
echo "GRADLE_OPTS: $GRADLE_OPTS"

# Test Java directly
echo "Testing Java installation..."
if "$JAVA_HOME/bin/java" -version; then
    echo "Java is working correctly"
else
    echo "Java test failed"
    exit 1
fi

# Try to run Gradle with explicit Java
echo "Attempting Gradle execution with explicit Java..."
export JAVA_HOME
"$JAVA_HOME/bin/java" -version

echo "Recovery script completed. Try running:"
echo "./gradlew clean build --no-daemon --info"
