/* This script will create the database */
CREATE DATABASE IF NOT EXISTS ipodia;

USE ipodia;

/****************************************/
/********* Creating the Tables **********/
/****************************************/

/* Creating the table of users (students, admins, registrars) */
CREATE TABLE IF NOT EXISTS users (
	email varchar(255),
	level varchar(255),
	firstName varchar(255),
	lastName varchar(255),
	password varchar(255),
	university varchar(255),
	classes varchar(255),
	PRIMARY KEY (email)
);

/* Create the table of class ids to names */
CREATE TABLE IF NOT EXISTS classListing (
	id int NOT NULL AUTO_INCREMENT,
	name varchar(255),
	PRIMARY KEY (id)
);
