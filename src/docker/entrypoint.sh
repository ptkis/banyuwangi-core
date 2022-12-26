#!/bin/bash
#show all environment
export

[[ -z "$YOURKIT_BROKER_URL" ]] && { echo "Please set YOURKIT_BROKER_URL" ; exit 1; }
[[ -z "$YOURKIT_BROKER_TOKEN" ]] && { echo "Please set YOURKIT_BROKER_TOKEN" ; exit 1; }

exec java -agentpath:/usr/local/YourKit-JavaProfiler-2022.9/bin/linux-x86-64/libyjpagent.so=broker_url=$YOURKIT_BROKER_URL,broker_token=$YOURKIT_BROKER_TOKEN $JAVA_OPTS -jar "$ARTIFACT-$VERSION.jar" $@
