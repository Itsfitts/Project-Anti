#!/bin/bash

# Dependency Update Helper Script
# This script helps with manual dependency management tasks

set -e

echo "🔧 Project-Anti Dependency Update Helper"
echo "========================================"

# Function to check if Gradle wrapper exists
check_gradle() {
    if [ ! -f "./gradlew" ]; then
        echo "❌ Gradle wrapper not found. Please run this script from the project root."
        exit 1
    fi
}

# Function to clean and refresh dependencies
refresh_dependencies() {
    echo "🧹 Cleaning project..."
    ./gradlew clean
    
    echo "🔄 Refreshing dependencies..."
    ./gradlew --refresh-dependencies
    
    echo "📋 Generating dependency report..."
    ./gradlew dependencies > dependency-report.txt
    echo "✅ Dependency report saved to dependency-report.txt"
}

# Function to run dependency vulnerability check
check_vulnerabilities() {
    echo "🔍 Checking for dependency vulnerabilities..."
    ./gradlew dependencyCheckAnalyze || echo "⚠️  Dependency check plugin not configured"
}

# Main execution
check_gradle

case "${1:-refresh}" in
    "refresh") refresh_dependencies ;;
    "check") check_vulnerabilities ;;
    "all") refresh_dependencies && check_vulnerabilities ;;
    *) echo "Usage: $0 [refresh|check|all]" ;;
esac

echo "✅ Done!"