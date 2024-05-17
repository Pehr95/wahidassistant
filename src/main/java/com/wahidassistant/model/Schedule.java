package com.wahidassistant.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;

@Data
@Document
public class Schedule {
    @Id
    private String id;
    private String url;
    private ArrayList<Event> events;
    private Date lastSynced;
    public Schedule(String url, ArrayList<Event> events, Date lastSynced) {
        this.url = url;
        this.events = events;
        this.lastSynced = lastSynced;
    }
}
