package Controller;

import Boundary.ScheduleScraper;
import Entity.Schedule;

public class MainController {
    private DatabaseController databaseController;
    private ScheduleScraper scheduleScraper;
    public MainController() {
        databaseController = new DatabaseController();
        scheduleScraper = new ScheduleScraper();

    }

    public void scrapeSchedule(String url, int weeksAhead) {
        Schedule schedule = scheduleScraper.scrapeSchedule(url, weeksAhead);
        if (schedule != null) {
            databaseController.insertSchedule(schedule);
        } else {
            System.out.println("Failed to scrape specified schedule.");
        }
    }

    public void showSchedule(String url) {
        Schedule schedule = databaseController.getScheduleFromDatabase(url);
        if (schedule != null) {
            schedule.printEvents();
        } else {
            System.out.println("Specified schedule is not found.");
        }
    }

    public void checkIfScheduleExists(String url) {
        if (!databaseController.checkIfScheduleExists(url)) {
            System.out.println("Specified schedule does not exist.");
        }
    }

    public void exit() {
        databaseController.disconnect();
    }
}
