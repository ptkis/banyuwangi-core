#!/bin/bash
#show all environment
export

exec java -agentpath:/usr/local/YourKit-JavaProfiler-2022.9/bin/linux-x86-64/libyjpagent.so=port=10001,dir=/snapshots,onexit=memory,listen=all $JAVA_OPTS -jar "$ARTIFACT-$VERSION.jar" $@
