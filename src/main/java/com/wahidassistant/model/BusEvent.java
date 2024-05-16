package com.wahidassistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.Date;

@Data
@AllArgsConstructor
public class BusEvent {
    private ArrayList<String> busNameAndNUmber;
    private Date departureTime;
    private Date arrivalTime;
    private int duration;
    private String origin; //location of departure
    private String destination; //location of arrival
    private int preferredArrivalTime;
}
