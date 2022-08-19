#!/usr/bin/env bash
set -e
source ./migrate-common.sh

function cleanup {

  echo "--Demo purposes only--"
  echo "Removing migrated users so script can be re-run, errors 'Not found' errors can be ignored in during cleanup"

  http POST "${OKTA_ORG}/api/v1/users/admin@example.com/lifecycle/deactivate" \
    "Authorization: SSWS ${OKTA_TOKEN}"
  http DELETE "${OKTA_ORG}/api/v1/users/admin@example.com" \
    "Authorization: SSWS ${OKTA_TOKEN}"

  http POST "${OKTA_ORG}/api/v1/users/user1@example.com/lifecycle/deactivate" \
    "Authorization: SSWS ${OKTA_TOKEN}"
  http DELETE "${OKTA_ORG}/api/v1/users/user1@example.com" \
    "Authorization: SSWS ${OKTA_TOKEN}"

  http POST "${OKTA_ORG}/api/v1/users/user2@example.com/lifecycle/deactivate" \
    "Authorization: SSWS ${OKTA_TOKEN}"
  http DELETE "${OKTA_ORG}/api/v1/users/user2@example.com" \
    "Authorization: SSWS ${OKTA_TOKEN}"

  echo "--Done cleanup--"
  echo
}
cleanup
