package com.wahidassistant;

import com.wahidassistant.component.CronJobComponent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
// Main class for the Spring application. Author Pehr Nortén.
public class Main {

	// Main method. Author Pehr Nortén.
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args); // Start the Spring application
	}

	@Bean
	// Run the specified code when the server starts. Used for debug. Author Pehr Nortén.
	CommandLineRunner runWhenServerStarts(CronJobComponent scrapingScheduler, CronJobComponent cronJobComponent) {
		return args -> {
			//scrapingScheduler.scrapeAndUpdateExistingSchedules();
			//scrapingScheduler.scrapeAndUpdateExistingSchedules();
			//scrapingScheduler.cleanUpUnusedSchedules();
			//cronJobComponent.scrapeAndUpdateExistingSchedules();
		};
	}

}
