package com.example.elsticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ElsticsearchApplication {
	public static void main(String[] args) {
		SpringApplication.run(ElsticsearchApplication.class, args);
	}
}
