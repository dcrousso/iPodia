/* This script will create the database */
CREATE DATABASE IF NOT EXISTS ipodia;

USE ipodia;

/****************************************/
/********* Creating the Tables **********/
/****************************************/

/* Creating the table of students */
CREATE TABLE IF NOT EXISTS students (
	email varchar(255),
	firstName varchar(255),
	lastName varchar(255),
	password varchar(255),
	university varchar(255),
	classes varchar(255),
	PRIMARY KEY (email)
);

/* Creating the table of teachers */
CREATE TABLE IF NOT EXISTS admins (
	email varchar(255),
	firstName varchar(255),
	lastName varchar(255),
	password varchar(255),
	university varchar(255),
	classes varchar(255),
	PRIMARY KEY (email)
);

/* Creating the table of registrars */
CREATE TABLE IF NOT EXISTS registrars (
	email varchar(255),
	firstName varchar(255),
	lastName varchar(255),
	password varchar(255),
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
	answer1 varchar(255),
	answer2 varchar(255),
	answer3 varchar(255),
	answer4 varchar(255),
	answer5 varchar(255),
	correctAnswer varchar(255),
	dueDate timestamp,
	topic varchar(255),
	PRIMARY KEY (id)
);
