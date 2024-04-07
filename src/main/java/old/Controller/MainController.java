package old.Controller;

import old.Boundary.Scraper;
import old.Entity.Schedule;

public class MainController {
    private DatabaseController databaseController;
    private Scraper scheduleScraper;
    public MainController() {
        databaseController = new DatabaseController();
        scheduleScraper = new Scraper();

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
