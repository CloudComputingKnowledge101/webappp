package com.example.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudComputingAssgn1Application {
	
	private static final Logger LOG = LogManager.getLogger(CloudComputingAssgn1Application.class);

	public static void main(String[] args) {
		LOG.info("App started using log4j");
		
		SpringApplication.run(CloudComputingAssgn1Application.class, args);
	}

}
