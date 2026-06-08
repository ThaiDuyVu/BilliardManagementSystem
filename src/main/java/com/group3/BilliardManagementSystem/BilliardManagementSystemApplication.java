package com.group3.BilliardManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BilliardManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BilliardManagementSystemApplication.class, args);
	}

}