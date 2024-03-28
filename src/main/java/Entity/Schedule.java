package Entity;

import java.util.ArrayList;

public class Schedule {
    private String url;
    private ArrayList<Event> events;
    public Schedule(String url, ArrayList<Event> events) {
        this.url = url;
        this.events = events;
    }
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
    public String getUrl() {
        return url;
    }
    public void printEvents() {
        for (Event event : events) {
            System.out.println(event);
        }
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}
