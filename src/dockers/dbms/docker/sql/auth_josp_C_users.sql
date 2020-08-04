CREATE DATABASE  IF NOT EXISTS `auth_josp`;
CREATE USER 'auth_user'@'%' IDENTIFIED BY 'user_auth';
GRANT ALL PRIVILEGES ON auth_josp.* TO 'auth_user'@'%';
