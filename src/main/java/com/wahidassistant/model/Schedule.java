package com.wahidassistant.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Document
public class Schedule {
    @Id
    private String id;
    //@Indexed(unique = false)
    private String url;
    private ArrayList<Event> events;
    public Schedule(String url, ArrayList<Event> events) {
        this.url = url;
        this.events = events;
    }
}
