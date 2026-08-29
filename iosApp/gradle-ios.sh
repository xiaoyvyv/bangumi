#!/bin/sh

if [ -z "${GRADLE_OPTS:-}" ]; then
    export GRADLE_OPTS="-Dorg.gradle.jvmargs='-Xmx2g -Dkotlin.daemon.jvm.options=\"-Xmx1g\" -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8' -Dorg.gradle.project.kotlin.native.jvmArgs='-Xmx8g -XX:MaxMetaspaceSize=1g -XX:+UseG1GC' -Dorg.gradle.workers.max=1 -Dorg.gradle.parallel=false"
fi
