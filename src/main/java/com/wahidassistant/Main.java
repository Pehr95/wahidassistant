package com.wahidassistant;

import com.wahidassistant.component.CronJobComponent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Bean
	CommandLineRunner runWhenServerStarts(CronJobComponent scrapingScheduler, CronJobComponent cronJobComponent) {
		return args -> {
			//scrapingScheduler.scrapeAndUpdateExistingSchedules();
			//scrapingScheduler.scrapeTester();
			//scrapingScheduler.scrapeAndUpdateExistingSchedules();
			//scrapingScheduler.cleanUpUnusedSchedules();
			//cronJobComponent.scrapeAndUpdateExistingSchedules();
		};
	}

}