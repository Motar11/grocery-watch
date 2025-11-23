#!/usr/bin/env sh

# Minimal Gradle wrapper bootstrap script for Grocery Watch
APP_BASE_NAME=`basename "$0"`
APP_HOME=`cd "$(dirname "$0")"; pwd`

if [ -n "$JAVA_HOME" ] ; then
    JAVA_CMD="$JAVA_HOME/bin/java"
else
    JAVA_CMD="java"
fi

WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Gradle wrapper jar is missing. Run 'gradle wrapper --gradle-version 8.6' to regenerate it." >&2
    exit 1
fi

exec "$JAVA_CMD" $JAVA_OPTS $GRADLE_OPTS -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
