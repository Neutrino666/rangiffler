#!/bin/bash

set -e
set -u
set -o pipefail

function create_database() {
    echo "  Creating database '$1'"
    mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
        CREATE DATABASE IF NOT EXISTS \`$1\`
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_unicode_ci;
EOSQL
}

if [ -n "$CREATE_DATABASES" ]; then
	echo "Multiple database creation requested: $CREATE_DATABASES"
	for db in $(echo $CREATE_DATABASES | tr ',' ' '); do
	  echo "$db"
		create_database $db
	done
	echo "Multiple databases created"
fi
