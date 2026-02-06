FROM eclipse-temurin:23-jdk
WORKDIR /app
COPY gradle gradle
COPY doc-api doc-api
COPY doc-contracts doc-contracts
COPY doc-renderer doc-renderer
COPY build.gradle settings.gradle ./
COPY gradlew .
RUN chmod +x gradlew
RUN ["./gradlew", ":doc-api:bootJar"]
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/doc-api/build/libs/doc-api-0.0.1-SNAPSHOT.jar"]
