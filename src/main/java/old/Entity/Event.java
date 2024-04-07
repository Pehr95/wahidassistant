package old.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Event {
    private String courseName;
    private Date startTime;
    private Date endTime;
    private int duration;
    private HashMap<String, String> rooms;
    private ArrayList<String> teachers;
    private String description;
    private Date lastUpdated;

    public Event(String courseName, Date startTime, Date endTime, int duration, HashMap<String, String> rooms, ArrayList<String> teachers, String description, Date lastUpdated) {
        this.courseName = courseName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.rooms = rooms;
        this.teachers = teachers;
        this.description = description;
        this.lastUpdated = lastUpdated;

    }

    public String getCourseName() {
        return courseName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return duration;
    }

    public HashMap<String, String> getRooms() {
        return rooms;
    }

    public ArrayList<String> getTeachers() {
        return teachers;
    }

    public String getDescription() {
        return description;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public String toString() {
        return "Event{" +
                "courseName='" + courseName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", rooms=" + rooms +
                ", teachers=" + teachers +
                ", description='" + description + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
