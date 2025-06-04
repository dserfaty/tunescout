package com.dev1023.tunescout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TunescoutApplication {

	public static void main(String[] args) {
		SpringApplication.run(TunescoutApplication.class, args);
	}
}
