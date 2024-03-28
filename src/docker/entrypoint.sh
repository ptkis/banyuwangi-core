#!/bin/sh
#show all environment
export

exec java $JAVA_OPTS -jar "$ARTIFACT-$VERSION.jar" $@
