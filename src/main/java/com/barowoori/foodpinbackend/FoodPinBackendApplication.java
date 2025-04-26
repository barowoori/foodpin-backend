package com.barowoori.foodpinbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FoodPinBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodPinBackendApplication.class, args);
	}

}
