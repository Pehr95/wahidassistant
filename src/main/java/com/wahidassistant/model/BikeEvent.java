package com.wahidassistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class BikeEvent /*extends Event*/ {
    //todo: fixa den
    private Date departureTime;
    private Date arrivalTime;
    private String durationInMinutes;
}
