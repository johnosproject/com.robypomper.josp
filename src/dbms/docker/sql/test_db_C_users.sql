CREATE DATABASE IF NOT EXISTS `test_db`;
CREATE USER 'test_db'@'%' IDENTIFIED BY 'db_test';
GRANT ALL PRIVILEGES ON test_db.* TO 'test_db'@'%';
