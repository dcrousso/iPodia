/* This script will create the database */
CREATE DATABASE IF NOT EXISTS ipodia_database;

USE ipodia_database;

/****************************************/
/********* Creating the Tables **********/
/****************************************/

/* Creating the user_table*/
CREATE TABLE IF NOT EXISTS user_table 
(
	userID int NOT NULL AUTO_INCREMENT,
	firstName varchar(255), 
	lastName varchar(255), 
	password varchar(255),
	email varchar(255), 
	university varchar(255),
	PRIMARY KEY (userID)
);

/* Creating the user_table*/
CREATE TABLE IF NOT EXISTS admin_table 
(
	userID int NOT NULL AUTO_INCREMENT,
	firstName varchar(255), 
	lastName varchar(255), 
	password varchar(255),
	email varchar(255), 
	university varchar(255),
	PRIMARY KEY (userID)
);