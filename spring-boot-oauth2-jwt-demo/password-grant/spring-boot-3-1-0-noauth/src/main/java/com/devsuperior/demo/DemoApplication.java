package com.devsuperior.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devsuperior.demo.config.SecurityConfig;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	private final SecurityConfig securityConfig;

	@Autowired
	private PasswordEncoder passwordEncoder;

	DemoApplication(SecurityConfig securityConfig) {
		this.securityConfig = securityConfig;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		System.out.println("ENCODE = " + passwordEncoder.encode("123456"));

		boolean result = passwordEncoder.matches("123456", "$2a$10$JTDdIBiVBaXOSQqIn24Ggu.AmwetK1ND5NHuOEPmOWaxlldeB9RA6");
		System.out.println(result);

		result = passwordEncoder.matches("1234567", "$2a$10$JTDdIBiVBaXOSQqIn24Ggu.AmwetK1ND5NHuOEPmOWaxlldeB9RA6");
		System.out.println(result);
	}
}