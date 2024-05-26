package com.wahidassistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@AllArgsConstructor
@Data
public class BusEvent /*extends Event*/ {
    private String busNumber;
    private String busName;
    private Date departureTime;
    private Date arrivalTime;
    private String durationInMinutes;
    private String origin; //location of departure
    private String destination; //location of arrival
    private int preferredArrivalTime;

    private boolean IsBus;
 /*
    public BusEvent(String busNumber, String busName, Date departureTime, Date arrivalTime, String durationInMinutes, String origin, String destination, int preferredArrivalTime, boolean isBus) {
        //todo: lägg in konstruktor som kopplar till Event.
        //super("Buss", );
        this.busNumber = busNumber;
        this.busName = busName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.durationInMinutes = durationInMinutes;
        this.origin = origin;
        this.destination = destination;
        this.preferredArrivalTime = preferredArrivalTime;
        IsBus = isBus;
    }

  */
}
