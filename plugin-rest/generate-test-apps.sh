#!/usr/bin/env bash
set -e

rm -rf build/
mkdir build
export pluginVersion=`cat build.gradle | grep "version \"" | sed -n 's/^[ \t]*version\ "//pg' | sed -n 's/"//pg'`
export grailsVersion=`cat spring-security-rest-testapp-profile/gradle.properties | grep grailsVersion | sed -n 's/^grailsVersion=//p'`
./gradlew clean install

echo "Plugin version: $pluginVersion. Grails version for test apps: $grailsVersion"
source "$HOME/.sdkman/bin/sdkman-init.sh"

[[ -d  ~/.sdkman/candidates/grails/$grailsVersion ]] || sdk install grails $grailsVersion
sdk use grails $grailsVersion
cd build

for feature in `ls ../spring-security-rest-testapp-profile/features/`; do
     grails create-app -profile org.grails.plugins:spring-security-rest-testapp-profile:$pluginVersion -features $feature $feature
done