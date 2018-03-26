FROM maven:3.5-jdk-9-slim
MAINTAINER Georg Victor Sattler <georg@digifit.in>

ARG BUILD_NUMBER
ARG BUILD_ID
ARG BUILD_TAG
ARG GIT_COMMIT

ARG DEPLOYER_USER
ARG DEPLOYER_PASS

ADD . /code
WORKDIR /code

RUN apt-get update &&\
    apt-get dist-upgrade -y &&\
    apt-get clean &&\
    apt-get autoclean &&\
    rm -rf /var/lib/apt/lists/* && \
    update-ca-certificates -f && \
    /var/lib/dpkg/info/ca-certificates-java.postinst configure

EXPOSE 8080

RUN mvn dependency:tree
RUN mvn package

RUN chmod 755 /code/docker-entrypoint.sh
ENTRYPOINT ["/code/docker-entrypoint.sh"]
CMD ["run"]
