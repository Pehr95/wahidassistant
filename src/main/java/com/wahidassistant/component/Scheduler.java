package com.wahidassistant.component;

import com.wahidassistant.model.Schedule;
import com.wahidassistant.repository.ScheduleRepository;
import com.wahidassistant.service.ScheduleService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class Scheduler {
    private final WebScraper webScraper;
    private final ScheduleRepository scheduleRepository;

    public Scheduler(WebScraper webScraper, ScheduleRepository scheduleRepository, ScheduleService scheduleService) {
        this.webScraper = webScraper;
        this.scheduleRepository = scheduleRepository;
    }

    //@Scheduled(cron = "0 0 * * * *") // Run every hour
    public void scrapeAndSave() {
            System.out.println("Scraping and saving schedule");
            Schedule schedule = webScraper.scrapeSchedule("https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=1&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.KGGRD23h",2);//"https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=6&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.TGSYA23h", 2);
            if (!checkURLAndSchedule(schedule)){
                scheduleRepository.insert(schedule);
            }
    }

    private boolean checkURLAndSchedule(Schedule schedule){
        boolean confirmed = false;
        //todo: 1. Checka URL (Pehr), checka schemat (Pehr),
        //      2. KLARA! Checka om den finns i databasen och ersätt om den är gammal eller ny (Wahid & Amer),
        //      3. KLARA! Checka om det inte finns i databasen och lägg till i hela scheman databasen,
        //      4.
        confirmed = updateSchedule(schedule);

        return confirmed;
    }

    public boolean updateSchedule(Schedule updatedSchedule) {
        boolean confirmed = false;
        List<Schedule> scheduleList = scheduleRepository.findAll();
        String id = "";
        for (int i = 0; i< scheduleList.size();i++){
            if (scheduleList.get(i).getUrl().equals(updatedSchedule.getUrl())){
                id = scheduleList.get(i).getId();
                updatedSchedule.setId(id);
                scheduleRepository.deleteById(id);
                scheduleRepository.save(updatedSchedule);
                confirmed = true;
            }else {
                confirmed = false;
            }
        }
        return confirmed;
    }
}
