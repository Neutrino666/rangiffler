FROM gradle:8.14-jdk21

WORKDIR /home/gradle/rangiffler
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./rangiffler-e-2-e-tests ./rangiffler-e-2-e-tests

COPY ./rangiffler-grpc-common ./rangiffler-grpc-common
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

CMD gradle test -Dtest.env=docker -Duser.timezone=Europe/Moscow
