#!/usr/bin/env bash
set -e
source ./migrate-common.sh

###################################################################
# Splits a _standard_ bcrypt formatted into comma seperated string.
# and returns it in the format of "[cost] [salt] [hash]"
###################################################################
function hash_parts {
  echo ${pw_hash} | awk '{split($0, parts, "$"); print parts[3] " " substr(parts[4],0,22) " " substr(parts[4],23)}'
}

###################################################################
# Imports a user with attributes and an existing password hash.
# Usage: import_user_with_pw [email] [pw_hash] [fname] [lname] [phone]
###################################################################
function import_user_with_pw {
  local email=${1}
  local pw_hash=${2}
  local fname=${3}
  local lname=${4}
  local phone=${5}
  # split up the hash string into it's parts
  local pw_hash_parts=($(hash_parts ${pw_hash}))

  echo "Importing User with hash:"
  echo "  email: ${email}"
  echo "  fname:  ${fname}"
  echo "  lname:  ${lname}"
  echo "  phone:  ${phone}"
  echo

  # Call the Okta Users API and create a user with an existing hash
  http --ignore-stdin --check-status \
    "${OKTA_ORG}/api/v1/users" \
    "Authorization: SSWS ${OKTA_TOKEN}" \
    profile[firstName]="${fname}" \
    profile[lastName]="${lname}" \
    profile[email]="${email}" \
    profile[login]="${email}" \
    profile[mobilePhone]="${phone}" \
    credentials[password][hash][algorithm]=BCRYPT \
    credentials[password][hash][workFactor]="${pw_hash_parts[0]}" \
    credentials[password][hash][salt]="${pw_hash_parts[1]}" \
    credentials[password][hash][value]="${pw_hash_parts[2]}"

}

# Query the 'users' table of the database for records that do NOT have a supported hash type
psql --dbname ${POSTGRES_DB} \
     --username ${POSTGRES_USER} \
     --tuples-only \
     --no-align \
     --field-separator ' ' \
     --quiet \
     -c "SELECT username, password, first_name, last_name, phone FROM users WHERE enabled=true AND password LIKE '{bcrypt}%';" \
  | while read username password first_name last_name phone; do
     import_user_with_pw "${username}" "${password}" "${first_name}" "${last_name}" "${phone}"
  done