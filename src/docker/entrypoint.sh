#!/bin/bash
#show all environment
export

exec java -agentpath:/usr/local/YourKit-JavaProfiler-2022.9/bin/linux-x86-64/libyjpagent.so=broker_url=https://broker.yourkit.com/DWPsmXsn4tD3QeHj3xYg/,broker_token=sjW5ANuKesEt7YLonyMe $JAVA_OPTS -jar "$ARTIFACT-$VERSION.jar" $@
