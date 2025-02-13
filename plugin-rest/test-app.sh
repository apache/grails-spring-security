#!/bin/bash

set -e
set -x

[[ ! -z "$BINTRAY_KEY" ]] && echo "bintrayKey=$BINTRAY_KEY" >> ~/.gradle/gradle.properties
[[ ! -z "$PLUGIN_PORTAL_PASSWORD" ]] && echo "pluginPortalPassword=$PLUGIN_PORTAL_PASSWORD" >> ~/.gradle/gradle.properties

./generate-test-apps.sh

[[ -z "$CI" ]] && docker-compose up -d
./gradlew :spring-security-rest:check check
[[ -z "$CI" ]] && docker-compose down

if [ "$TRAVIS_PULL_REQUEST" = "false" ]; then ./gradlew artifactoryPublish; fi