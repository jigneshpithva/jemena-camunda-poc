# Stop & remove old container
docker rm -f $(docker ps -aq --filter "ancestor=jemena-camunda-poc")

# Build latest image
docker build -t jemena-camunda-poc .

# Run container using env file
docker run --env-file .env -p 8080:8080 jemena-camunda-poc