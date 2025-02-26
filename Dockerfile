FROM eclipse-temurin:17.0.14_7-jdk

#Install curl for health check
RUN apt update && apt install -y curl

ARG VERSION
ARG ARTIFACT

ENV VERSION=$VERSION
ENV ARTIFACT=$ARTIFACT
ENV JAVA_OPTS=""

COPY build/libs/$ARTIFACT-$VERSION.jar $ARTIFACT-$VERSION.jar
COPY src/docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 8080

ENTRYPOINT set -a && /entrypoint.sh
