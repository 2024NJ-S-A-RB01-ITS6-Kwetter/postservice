# Dockerfile for Spring Boot microservice
FROM azul/zulu-openjdk-alpine:17
ARG JAR_FILE=build/libs/\*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]