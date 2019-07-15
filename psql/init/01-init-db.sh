#!/bin/bash
set -o errexit

main() {
    init_table_script
}

init_table_script() {
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
CREATE TABLE monitoring_table (
    app_user      VARCHAR(100)  NOT NULL,
    age_range     VARCHAR(100)  NOT NULL,
    amount        INTEGER       NOT NULL
);
INSERT INTO monitoring_table VALUES ('gp', '30', 1);
EOSQL
}

main "$@"
