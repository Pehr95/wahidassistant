package com.wahidassistant;

import com.wahidassistant.component.Scheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner2(Scheduler scrapingScheduler) {
		return args -> {
			scrapingScheduler.scrapeAndSave();
		};
	}

}
