drop database if exists money_management_db;
CREATE DATABASE `money_management_db`;
use money_management_db;

CREATE TABLE `budget` (
  `username` varchar(255) NOT NULL,
  `encrypted_amount` varchar(5000) NOT NULL,
  PRIMARY KEY (`username`)
)
