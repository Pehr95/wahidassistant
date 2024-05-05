package com.wahidassistant.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Data
@Document
public class Event {
    @Id
    private String id;
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
}