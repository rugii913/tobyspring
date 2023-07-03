DROP DATABASE IF EXISTS springbook;
CREATE DATABASE springbook;
USE springbook;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id VARCHAR(10) PRIMARY KEY,
    NAME VARCHAR(20) NOT NULL,
    PASSWORD VARCHAR(20) NOT NULL
);


#-------------------------------------
SELECT * FROM users;

#-------------------------------------
DROP DATABASE IF EXISTS testdb;
CREATE DATABASE testdb;
USE testdb;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id VARCHAR(10) PRIMARY KEY,
    NAME VARCHAR(20) NOT NULL,
    PASSWORD VARCHAR(20) NOT NULL
);