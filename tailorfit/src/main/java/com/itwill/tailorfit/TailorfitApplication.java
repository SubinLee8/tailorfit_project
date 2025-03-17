package com.itwill.tailorfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TailorfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(TailorfitApplication.class, args);
	}

}
