FROM gradle:8.5-jdk17 AS builder
ARG MODULE_NAME=data-jpa
WORKDIR /app
COPY . .
RUN gradle :${MODULE_NAME}:bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
ARG MODULE_NAME=data-jpa
WORKDIR /app
COPY --from=builder /app/${MODULE_NAME}/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
