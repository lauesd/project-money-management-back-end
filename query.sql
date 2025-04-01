drop database if exists money_management_db;
CREATE DATABASE `money_management_db`;
use money_management_db;

CREATE TABLE `budget` (
  `username` varchar(255) NOT NULL,
  `encrypted_amount` varchar(5000) NOT NULL,
  PRIMARY KEY (`username`)
);

CREATE TABLE transaction (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    encrypted_name VARCHAR(5000) NOT NULL,
    encrypted_amount VARCHAR(5000) NOT NULL,
	username varchar(255) NOT NULL,
    FOREIGN KEY (username) REFERENCES budget(username)
);	