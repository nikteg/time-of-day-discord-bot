FROM --platform=$BUILDPLATFORM gradle:jdk21 AS build
COPY . /home/gradle
RUN gradle build

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/app/build/libs/app.jar .
RUN userdel -r ubuntu 2>/dev/null; groupadd -r -g 1000 user && useradd -r -g user -u 1000 user
RUN chown -R user:user /app
USER user
ENTRYPOINT ["java", "-jar", "app.jar"]
