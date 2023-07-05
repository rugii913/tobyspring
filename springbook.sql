DROP DATABASE IF EXISTS springbook;
CREATE DATABASE springbook;
USE springbook;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    `id` VARCHAR(10) PRIMARY KEY,
    `Name` VARCHAR(20) NOT NULL,
    `Password` VARCHAR(20) NOT NULL
);

ALTER TABLE users ADD COLUMN `Level` TINYINT NOT NULL, ADD COLUMN `Login` INT NOT NULL, ADD COLUMN `Recommend` INT NOT NULL;


#-------------------------------------
SELECT * FROM users;

#-------------------------------------
#-------------------------------------
#-------------------------------------
#-------------------------------------
#----------testdb---------------------
DROP DATABASE IF EXISTS testdb;
CREATE DATABASE testdb;
USE testdb;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    `id` VARCHAR(10) PRIMARY KEY,
    `Name` VARCHAR(20) NOT NULL,
    `Password` VARCHAR(20) NOT NULL
);

ALTER TABLE users ADD COLUMN `Level` TINYINT NOT NULL, ADD COLUMN `Login` INT NOT NULL, ADD COLUMN `Recommend` INT NOT NULL;


#-------------------------------------
SELECT * FROM users;