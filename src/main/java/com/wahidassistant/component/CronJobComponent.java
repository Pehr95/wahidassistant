package com.wahidassistant.component;

import com.wahidassistant.model.Schedule;
import com.wahidassistant.model.Status;
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
    private final ScheduleRepository scheduleRepository;
    private final ScheduleService scheduleService;

    // Test scraping and saving schedule (Delete later)
    public void scrapeTester() {
        System.out.println("Scraping and saving schedule");
        scheduleService.addOrUpdateSchedule("https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=6&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.TGSYA23h");// Invalid link --> "https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=1&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.KGGRD23"
        // Invalid link --> "https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=1&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.KGGRD23"
    }

    // scrape and save every 4 hours
    @Scheduled(cron = "0 0 */4 * * *")
    public void scrapeAndUpdateExistingSchedules() { // Med Wahid
        List<Schedule> scheduleList = scheduleRepository.findAll();

        // Loop through all schedules and check if they have changed
        if (!scheduleList.isEmpty()) {
            Status status;
            for (Schedule oldSchedule : scheduleList) {
                status = scheduleService.addOrUpdateSchedule(oldSchedule.getUrl());
                switch (status) {
                    case UPDATED:
                        System.out.println("Successfully updated schedule with id: " + oldSchedule.getId());
                        System.out.println("Updating all relevant users custom events");
                        scheduleService.updateAllRelevantUsersCustomEvents(oldSchedule.getId());
                        break;
                    case FAILED:
                        System.out.println("Failed to scrape schedule with id: " + oldSchedule.getId());
                        break;
                    case NO_CHANGE:
                        System.out.println("No changes in schedule with id: " + oldSchedule.getId());
                        break;
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
