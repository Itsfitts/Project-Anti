#!/bin/bash
# Standalone Java Recovery Script

# Try to find any working Java installation
for java_candidate in /usr/lib/jvm/*/bin/java /opt/*/bin/java /usr/bin/java; do
    if [ -x "$java_candidate" ]; then
        echo "Testing Java at: $java_candidate"
        if "$java_candidate" -version 2>&1 | grep -q "java version\|openjdk version"; then
            JAVA_BIN="$java_candidate"
            JAVA_HOME=$(dirname $(dirname "$java_candidate"))
            echo "Found working Java: $JAVA_BIN"
            echo "JAVA_HOME set to: $JAVA_HOME"
            break
        fi
    fi
done

if [ -z "$JAVA_BIN" ]; then
    echo "No working Java found. Manual installation required."
    exit 1
fi

# Export the working Java
export JAVA_HOME="$JAVA_HOME"
export PATH="$JAVA_HOME/bin:$PATH"

# Clear corrupted environment variables
unset _JAVA_OPTIONS
unset JAVA_TOOL_OPTIONS
unset JDK_JAVA_OPTIONS

# Test the Gradle wrapper directly with the working Java
echo "Testing Gradle with recovered Java..."
cd /home/tokenblkguy/AndroidStudioProjects/Project-Anti
rm -rf .gradle build
chmod +x gradlew

# Use the working Java directly with Gradle
"$JAVA_BIN" -jar gradle/wrapper/gradle-wrapper.jar --version

echo "Java recovery attempt complete."
echo "If successful, try: ./gradlew clean build"
