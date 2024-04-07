package com.wahidassistant.component;

import com.wahidassistant.model.Schedule;
import com.wahidassistant.repository.ScheduleRepository;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    private final WebScraper webScraper;
    private final ScheduleRepository scheduleRepository;

    public Scheduler(WebScraper webScraper, ScheduleRepository scheduleRepository) {
        this.webScraper = webScraper;
        this.scheduleRepository = scheduleRepository;
    }

    //@Scheduled(cron = "0 0 * * * *") // Run every hour
    public void scrapeAndSave() {
        System.out.println("Scraping and saving schedule");
        Schedule schedule = webScraper.scrapeSchedule("https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=6&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.TGSYA23h", 2);
        scheduleRepository.insert(schedule);
    }
}
