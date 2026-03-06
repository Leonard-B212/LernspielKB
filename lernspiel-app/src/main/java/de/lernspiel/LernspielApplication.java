package de.lernspiel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.lernspiel")
public class LernspielApplication {

	public static void main(String[] args) {
		SpringApplication.run(LernspielApplication.class, args);
	}

}
