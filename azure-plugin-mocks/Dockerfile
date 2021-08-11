FROM openjdk:11-jre-slim

RUN addgroup --gid 1000 azure-plugin-mock && \
    adduser --uid 1000 --gecos '' --gid 1000 azure-plugin-mock

ENV WORKSPACE=/workspace

RUN mkdir -p $WORKSPACE && chown -R azure-plugin-mock:azure-plugin-mock $WORKSPACE

USER azure-plugin-mock

WORKDIR $WORKSPACE

ENV EXTRA_ARGS=""

ARG ARTIFACT_VERSION=latest
COPY --chown=azure-plugin-mock:azure-plugin-mock app/build/libs/app-${ARTIFACT_VERSION}.jar $WORKSPACE/azure-plugin-mocks.jar

ENTRYPOINT [ "sh", "-c", "exec java -server -jar $WORKSPACE/azure-plugin-mocks.jar $EXTRA_ARGS" ]
