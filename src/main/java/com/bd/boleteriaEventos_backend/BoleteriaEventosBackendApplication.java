package com.bd.boleteriaEventos_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BoleteriaEventosBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoleteriaEventosBackendApplication.class, args);
	}

}
