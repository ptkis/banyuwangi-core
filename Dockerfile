FROM ptkis/ubuntu-ffmpeg:22.04-jdk17-20221130

RUN curl --location https://www.yourkit.com/download/docker/YourKit-JavaProfiler-2022.9-docker.zip -o /tmp/YourKit-JavaProfiler.zip && \
  unzip /tmp/YourKit-JavaProfiler.zip -d /usr/local && \
  rm /tmp/YourKit-JavaProfiler.zip

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
