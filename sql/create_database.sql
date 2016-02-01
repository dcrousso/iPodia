/* This script will create the database */
CREATE DATABASE IF NOT EXISTS ipodia;

USE ipodia;

/****************************************/
/********* Creating the Tables **********/
/****************************************/

/* Creating the table of users */
CREATE TABLE IF NOT EXISTS users (
	userID int NOT NULL AUTO_INCREMENT,
	firstName varchar(255),
	lastName varchar(255),
	password varchar(255),
	email varchar(255),
	university varchar(255),
	classes varchar(255),
	PRIMARY KEY (userID)
);

/* Creating the table of teachers */
CREATE TABLE IF NOT EXISTS admins (
	userID int NOT NULL AUTO_INCREMENT,
	firstName varchar(255),
	lastName varchar(255),
	password varchar(255),
	email varchar(255),
	university varchar(255),
	classes varchar(255),
	PRIMARY KEY (userID)
);
