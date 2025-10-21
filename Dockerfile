FROM --platform=linux/amd64 gradle:jdk21 AS build
COPY . /home/gradle
RUN gradle build

FROM --platform=linux/amd64 openjdk:21
WORKDIR /app
COPY --from=build /home/gradle/app/build/libs/app.jar .
RUN groupadd -r -g 1000 user && useradd -r -g user -u 1000 user
RUN chown -R user:user /app
USER user
ENTRYPOINT exec java -jar app.jar