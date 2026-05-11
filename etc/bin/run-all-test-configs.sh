#!/usr/bin/env sh
set -eu

trap 'echo "Interrupted"; exit 130' INT

./gradlew check

for config in \
  static \
  annotation \
  requestmap \
  basic \
  basicCacheUsers \
  misc \
  putWithParams \
  bcrypt \
  issue503
do
  ./gradlew core-examples-functional-test-app:check -DTESTCONFIG="$config"
done