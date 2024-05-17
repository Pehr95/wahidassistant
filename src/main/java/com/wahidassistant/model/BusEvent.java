package com.wahidassistant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.Date;

@AllArgsConstructor
@Data
public class BusEvent {
    private String busNumber;
    private String busName;
    private Date departureTime;
    private Date arrivalTime;
    private String durationInMinutes;
    private String origin; //location of departure
    private String destination; //location of arrival
    private int preferredArrivalTime;

}
