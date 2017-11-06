# Start from an specific version to favor reproducible builds
# https://reproducible-builds.org/
FROM maven:3.3.9-jdk-8-alpine

# Install intentionally specific versions and not performing an upgrade to favor reproducible builds
# https://reproducible-builds.org/
RUN apk update && \
    apk add rpm=4.12.0.1-r0 && \
    apk add docker=1.12.6-r0 && \
    apk add sudo=1.8.19_p1-r0

RUN adduser -h /home/jenkins -u 1000 -s /bin/bash -D jenkins
WORKDIR /home/jenkins
