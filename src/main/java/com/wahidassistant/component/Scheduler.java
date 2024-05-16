package com.wahidassistant.component;

import com.wahidassistant.model.Schedule;
import com.wahidassistant.repository.ScheduleRepository;
import com.wahidassistant.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Scheduler {
    private final WebScraper webScraper;
    private final ScheduleRepository scheduleRepository;

    private final ScheduleService scheduleService;

    //@Scheduled(cron = "0 0 * * * *") // Run every hour
    public void scrapeAndSave() {
        System.out.println("Scraping and saving schedule");
        Schedule schedule = webScraper.scrapeSchedule("https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=6&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.TGSYA23h",2);//"https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=1&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.KGGRD23h", 2);
        if (!scheduleService.updateSchedule(schedule)){
            scheduleRepository.insert(schedule);
        }
    }
}
