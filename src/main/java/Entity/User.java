package Entity;

import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private ArrayList<String> jasonWebTokens;
    private String scheduleRef;
    private ArrayList<Event> customEvents;
    private ArrayList<Event> hiddenEvents;
    public User(String username, String password, ArrayList<String> jasonWebTokens, String scheduleRef, ArrayList<Event> customEvents, ArrayList<Event> hiddenEvents) {
        this.username = username;
        this.password = password;
        this.jasonWebTokens = jasonWebTokens;
        this.scheduleRef = scheduleRef;
        this.customEvents = customEvents;
        this.hiddenEvents = hiddenEvents;
    }
}
