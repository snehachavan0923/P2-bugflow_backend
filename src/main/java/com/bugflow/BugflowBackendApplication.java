package com.bugflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BugflowBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugflowBackendApplication.class, args);
	}

}