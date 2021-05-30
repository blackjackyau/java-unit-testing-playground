package com.blax.unit.testing.playground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UnitTestingPlaygroundApplication {

	public static void main(String[] args) {
		java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
		SpringApplication.run(UnitTestingPlaygroundApplication.class, args);
	}

}
