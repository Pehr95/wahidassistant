package com.wahidassistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * This class represents a bike event with departure and arrival times.
 * Author: Wahid
 */
@AllArgsConstructor
@Data
public class BikeEvent /*extends Event*/ {
    //todo: fixa den
    private Date departureTime; // Departure time for bike event
    private Date arrivalTime; // Arrival time for bike event
    private String durationInMinutes; // Duration of the bike event in minutes
}
