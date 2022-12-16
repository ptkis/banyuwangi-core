FROM ptkis/ubuntu-ffmpeg:22.04-jdk17-20221130

EXPOSE 10001

ARG VERSION
ARG ARTIFACT

ENV VERSION=$VERSION
ENV ARTIFACT=$ARTIFACT
ENV JAVA_OPTS=""

COPY build/libs/$ARTIFACT-$VERSION.jar $ARTIFACT-$VERSION.jar
COPY src/docker/entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh

EXPOSE 8080

ENTRYPOINT set -a && ./entrypoint.sh
