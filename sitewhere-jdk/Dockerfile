FROM dekstroza/openjdk9-alpine as packager

RUN /opt/jdk-9/bin/jlink \
    --module-path /opt/jdk-9/jmods \
    --verbose \
    --add-modules java.base,java.logging,java.sql,java.xml,jdk.unsupported \
    --compress 2 \
    --no-header-files \
    --output /opt/jdk-9-minimal

FROM alpine:3.6
COPY --from=packager /opt/jdk-9-minimal /opt/jdk-9-minimal

ENV JAVA_HOME=/opt/jdk-9-minimal
ENV PATH="$PATH:$JAVA_HOME/bin"
