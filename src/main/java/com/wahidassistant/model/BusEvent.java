package com.wahidassistant.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.Date;

@Data
@Document
public class BusEvent {

    @Id
    private String Id;

    private ArrayList<String> busNameAndNUmber;

    private Date departureTime;

    private Date arrivalTime;

    private int duration;

    private String origin; //location of departure

    private String destination; //location of arrival

    private int preferredArrivalTime;

    public BusEvent(){}

    public BusEvent(ArrayList<String> busNameAndNUmber, Date departureTime, Date arrivalTime, int duration, String origin, String destination, int preferredArrivalTime) {
        this.busNameAndNUmber = busNameAndNUmber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.origin = origin;
        this.destination = destination;
        this.preferredArrivalTime = preferredArrivalTime;
    }

    public ArrayList<String> getBusNameAndNUmber() {
        return busNameAndNUmber;
    }

    public void setBusNameAndNUmber(ArrayList<String> busNameAndNUmber) {
        this.busNameAndNUmber = busNameAndNUmber;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getPreferredArrivalTime() {
        return preferredArrivalTime;
    }

    public void setPreferredArrivalTime(int preferredArrivalTime) {
        this.preferredArrivalTime = preferredArrivalTime;
    }
}
