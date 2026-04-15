# jemena-camunda-poc - Dockerized

This repository contains a Spring Boot POC that listens and publishes JMS messages and starts Camunda process instances.

This project already includes a multi-stage `Dockerfile` in the repository root. The Dockerfile builds the application using Maven in a build stage and creates a minimal runtime image.

## Build locally (jar)

1. Build the project jar (skip tests):

```bash
mvn -f "C:\\Users\\VIAAN\\dev\\Camunda\\POC\\jemena\\jemena-camunda-poc\\pom.xml" -DskipTests package
```

The resulting artifact will be created at `target/jemena-camunda-poc-0.0.1-SNAPSHOT.jar`.

## Build Docker image

From the project root run:

```bash
docker build -t jemena-camunda-poc:latest .
```

This uses the multi-stage `Dockerfile` to compile (inside Maven image) and produce a runtime image containing the built jar.

## Run container

A minimal run command exposing port 8080 (Spring Boot default):

```bash
docker run --rm -p 8080:8080 \
  -e JAVA_OPTS="-Xms256m -Xmx512m" \
  -e JEMENA_MSI_PROCESS_ID=jemena-generic-jms-process \
  --name jemena-camunda-poc jemena-camunda-poc:latest
```

Notes:
- The image expects configuration from `application.yaml` built into the jar. You can override properties by providing environment variables (Spring Boot relaxed binding); for example to override `jemena.msi.process.id` set `JEMENA_MSI_PROCESS_ID`.
- To mount a local `application.yaml` override, use a volume mount and set `SPRING_CONFIG_LOCATION`:

```bash
docker run --rm -p 8080:8080 \
  -v "$(pwd)/application.yaml:/config/application.yaml:ro" \
  -e SPRING_CONFIG_LOCATION="/config/application.yaml" \
  jemena-camunda-poc:latest
```

## Tips / Next steps
- If you want a `docker-compose.yml` to wire in an Artemis broker or Camunda SaaS credentials, I can add that.
- If you want the image published to Docker Hub or a container registry, provide credentials/registry and I can add a small GitHub Actions workflow.

target/
*.log
*.tmp
*.jar
.mvn/
.settings/
.idea/
.vscode/
.git
.gitignore
Dockerfile
node_modules/
*.iml

