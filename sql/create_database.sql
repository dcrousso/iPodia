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

/* Creating the table of quizzes for a specific class */
CREATE TABLE IF NOT EXISTS class_test (
	id varchar(255),
	question varchar(255),
	answerA varchar(255),
	answerB varchar(255),
	answerC varchar(255),
	answerD varchar(255),
	answerE varchar(255),
	correctAnswer varchar(255),
	dueDate timestamp,
	topic varchar(255),
	PRIMARY KEY (id)
);
