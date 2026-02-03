FROM eclipse-temurin:23-jdk
WORKDIR /app
COPY gradle gradle
COPY src src
COPY build.gradle settings.gradle ./
COPY gradlew .
RUN chmod +x gradlew
RUN ["./gradlew", "bootJar"]
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/build/libs/DocApi-0.0.1-SNAPSHOT.jar"]