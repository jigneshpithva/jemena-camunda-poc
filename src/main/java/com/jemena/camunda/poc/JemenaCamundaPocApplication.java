package com.jemena.camunda.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class JemenaCamundaPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(JemenaCamundaPocApplication.class, args);
	}

}
