package com.project_money_management_back_end;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectMoneyManagementBackEndApplication {

	public static void main(String[] args) {
		System.out.println("MYSQL_HOST: " + System.getenv("MYSQL_HOST"));
		System.out.println("MYSQL_PORT: " + System.getenv("MYSQL_PORT"));
		System.out.println("MYSQL_DB: " + System.getenv("MYSQL_DB"));
		System.out.println("MYSQL_USER: " + System.getenv("MYSQL_USER"));
		System.out.println("MYSQL_PASSWORD: " + System.getenv("MYSQL_PASSWORD"));
		System.out.println("ENCRYPTION_SECRET: " + System.getenv("ENCRYPTION_SECRET"));
		System.out.println("ACCESS_TOKEN_SECRET: " + System.getenv("ACCESS_TOKEN_SECRET"));
		System.out.println("SERVER_PORT: " + System.getenv("SERVER_PORT"));

		SpringApplication.run(ProjectMoneyManagementBackEndApplication.class, args);
	}

}
