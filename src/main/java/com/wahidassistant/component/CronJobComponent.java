package com.wahidassistant.component;

import com.wahidassistant.model.Schedule;
import com.wahidassistant.repository.ScheduleRepository;
import com.wahidassistant.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling
@Component
@AllArgsConstructor
public class CronJobComponent {
    private final WebScraper webScraper;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleService scheduleService;

    // Test scraping and saving schedule (Delete later)
    public void scrapeTester() {
        System.out.println("Scraping and saving schedule");
        Schedule schedule = webScraper.scrapeSchedule("https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=6&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.TGSYA23h",2);//"https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=1&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.KGGRD23h", 2);
        if (!scheduleService.updateSchedule(schedule)){
            scheduleRepository.insert(schedule);
        }
    }

    // scrape and save every 4 hours
    @Scheduled(cron = "0 0 */4 * * *")
    public void scrapeAndUpdateExistingSchedules() {
        List<Schedule> scheduleList = scheduleRepository.findAll();
        // Loop through all schedules and check if they have changed

        if (!scheduleList.isEmpty()) {
            for (Schedule oldSchedule : scheduleList) {
                Schedule updatedSchedule = webScraper.scrapeSchedule(oldSchedule.getUrl(), 2);
                if (scheduleService.checkIfScheduleChanged(updatedSchedule)) {
                    scheduleService.updateSchedule(updatedSchedule);
                    System.out.println("Updated schedule with id: " + oldSchedule.getId());
                } else {
                    System.out.println("Schedule (" + oldSchedule.getId() + ") is up to date. Just updating lastSynced.");
                    scheduleService.updateScheduleLastSynced(oldSchedule.getId());
                }
            }
        } else {
            System.out.println("No schedules found in database to check for updates.");
        }
    }

    // Clean up unused schedules every night at 01:00
    @Scheduled(cron = "0 0 1 * * *")
    public void cleanUpUnusedSchedules() {
        scheduleService.cleanUpUnusedSchedules();
    }
}
