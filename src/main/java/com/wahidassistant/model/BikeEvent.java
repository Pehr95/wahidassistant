package com.wahidassistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class BikeEvent {
    //todo: fixa den
    private Date departureTime;
    private Date arrivalTime;
    private String durationInMinutes;
    private String origin; //location of departure
    private String destination; //location of arrival
    private int preferredArrivalTime;
}
