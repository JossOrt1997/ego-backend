package com.analiticasoft.ida;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IdaApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdaApplication.class, args);
	}

}
