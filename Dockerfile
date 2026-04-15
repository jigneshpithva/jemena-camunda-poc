# Multi-stage Dockerfile
# 1) Build the project with Maven
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only what is needed for a cached build of dependencies first
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
# copy sources
COPY src ./src

# Build the jar (skip tests to speed-up builds in CI/local)
RUN mvn -B -DskipTests package


# 2) Runtime image
FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=/workspace/target/jemena-camunda-poc-0.0.1-SNAPSHOT.jar

# Create non-root user
RUN useradd --create-home appuser || true
WORKDIR /home/appuser

# Copy jar from build stage
COPY --from=build ${JAR_FILE} /home/appuser/app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Use unprivileged user
USER appuser

# JVM options can be passed through the JAVA_OPTS env var at runtime
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /home/appuser/app.jar"]

