drop database if exists academy;
CREATE DATABASE if not exists academy
  default character set 'utf8';


CREATE TABLE academy.`group`
(
  id   int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name varchar(255)        NOT NULL
);
CREATE UNIQUE INDEX name
  ON academy.`group` (name);
INSERT INTO academy.`group` (id, name) VALUES (1, 'Java зима 2017');
INSERT INTO academy.`group` (id, name) VALUES (2, 'Java лето 2018');


CREATE TABLE academy.student
(
  id         int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  first_name varchar(255)        NOT NULL,
  last_name  varchar(255)        NOT NULL,
  age        int(11),
  email      varchar(255)        NOT NULL,
  group_id   int(11),
  CONSTRAINT student_group_fk FOREIGN KEY (group_id) REFERENCES `group` (id)
);
CREATE UNIQUE INDEX first_last_unique
  ON academy.student (first_name, last_name);
CREATE UNIQUE INDEX email
  ON academy.student (email);
CREATE INDEX student_group_fk
  ON academy.student (group_id);
INSERT INTO academy.student (id, first_name, last_name, age, email, group_id)
VALUES (1, 'Вася', 'Васечкин', 25, 'mail@mail.com', 1);
INSERT INTO academy.student (id, first_name, last_name, age, email, group_id)
VALUES (2, 'Маша', 'Ефросинина', 37, 'masha@mail.com', 1);