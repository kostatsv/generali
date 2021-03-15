package com.generali;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication
@SpringBootConfiguration
@ComponentScan(basePackages = {"com.generali.configuration",
		"com.generali.service",
		"com.generali.dao",
		"com.generali.controller"})
public class GeneraliApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeneraliApplication.class, args);
	}

}
