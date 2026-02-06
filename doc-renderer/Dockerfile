FROM eclipse-temurin:23-jdk
WORKDIR /app
COPY gradle gradle
COPY doc-api doc-api
COPY doc-contracts doc-contracts
COPY doc-renderer doc-renderer
COPY build.gradle settings.gradle ./
COPY gradlew .
RUN chmod +x gradlew
RUN ["./gradlew", ":doc-renderer:bootJar"]
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/doc-renderer/build/libs/doc-renderer-0.0.1-SNAPSHOT.jar"]
