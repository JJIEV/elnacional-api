package com.api.elnacional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Application.class);
//        springApplication.setAdditionalProfiles("PROD");
		springApplication.setAdditionalProfiles("DEV");
		springApplication.run(args);
	}

}
