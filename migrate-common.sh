###################################################################
# Common bits for example migration-* scripts in this repository
###################################################################

yq() {
  docker run --rm -i \
    -v "${PWD}":/workdir \
    -v ${HOME}/.okta:/okta_config \
    mikefarah/yq "$@"
}

psql() {
  docker exec -i db psql "$@"
}

http() {
  docker run --env OKTA_TOKEN akamai/httpie http --pretty all "$@"
}

# If you have an Okta Org already run `okta login` otherwise
# run `okta register` to create a new one
OKTA_CONFIG="/okta_config/okta.yaml"
OKTA_ORG=$(yq '.okta.client.orgUrl' ${OKTA_CONFIG})
OKTA_TOKEN=$(yq '.okta.client.token' ${OKTA_CONFIG})

# The example DB is in a docker container, use `docker compose up` to start the DB
DOCKER_COMPOSE=./docker-compose.yml
POSTGRES_DB=$(yq '.services.db.environment.POSTGRES_DB' ${DOCKER_COMPOSE})
POSTGRES_USER=$(yq '.services.db.environment.POSTGRES_USER' ${DOCKER_COMPOSE})
export PGPASSWORD=$(yq '.services.db.environment.POSTGRES_PASSWORD' ${DOCKER_COMPOSE})
