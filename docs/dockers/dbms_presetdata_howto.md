# Dockers - DBMS: How to manage Preset Data

The **DBMS** docker, on each startup, check the content of ```src/dockers/dbms/docker/sql```
dir and execute all ```*.sql``` files contained. By conventions the files contained
in this dir are organized by DB (each file is related with one and only one DB).

## DB's files

DB's files are parted in schema, data and users with following names:
* {DB_NAME}_A_schema.sql
* {DB_NAME}_B_data.sql
* {DB_NAME}_C_users.sql

Each file must include as first line the DB's creation statement:
```sql
CREATE DATABASE IF NOT EXISTS `{DB_NAME}`;
```

**Schema**<br>
  The schema files contains only the tables definitions. It can be generated with
  a DB's dumb (with 'only schema' option selected).

**Data**<br>
  The data files contains only the tables contents. It can be generated with
  a DB's dumb (with 'only data' option selected).

**Users**<br>
  DBMS's users are declared in users files that define: the username and password
  and the related permission granted for each user. Normally users can grant all
  privileges for current DB.

  Example:
  ```sql
  CREATE USER 'test_db'@'%' IDENTIFIED BY 'db_test';
  GRANT ALL PRIVILEGES ON test_db.* TO 'test_db'@'%';
  ```
