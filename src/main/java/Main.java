import Controller.MainController;

public class Main {
    public static void main(String[] args) {
        // This code will scrape the schedule from the specified URL, insert it into the database, and then show it buy getting it from the database.
        String url = "https://schema.mau.se/setup/jsp/Schema.jsp?startDatum=idag&intervallTyp=m&intervallAntal=6&sprak=SV&sokMedAND=true&forklaringar=true&resurser=p.TGSYA23h";
        MainController controller = new MainController();
        controller.checkIfScheduleExists(url);  // Check if the schedule exists in the database
        controller.scrapeSchedule(url, 2); // Scrape current week plus 2 weeks ahead and place it in the database
        controller.showSchedule(url); // Show the schedule from the database
        controller.exit();  // Disconnect from the database
    }
}
