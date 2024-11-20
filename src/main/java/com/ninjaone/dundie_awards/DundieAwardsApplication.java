package com.ninjaone.dundie_awards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DundieAwardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DundieAwardsApplication.class, args);
	}

}
