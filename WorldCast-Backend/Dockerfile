# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jdk
VOLUME /tmp
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY src/main/resources/service-account-key.json /app/service-account-key.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
