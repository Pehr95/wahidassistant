package com.wahidassistant.model;

import com.google.maps.model.TransitDetails;

import java.sql.Timestamp;
/**
 * Represents a bus trip with details such as bus number, name, departure and arrival time,
 * duration, and transit details.
 * Author: Wahid
 */
public class BusTrip {
    TransitDetails transitDetails = new TransitDetails(); // Transit details of the bus trip
    private String busNumber; // Number of the bus

    private String busName; // Name of the bus

    private Timestamp departureTime; // Departure time of the bus trip

    private Timestamp arrivalTime; // Arrival time of the bus trip

    private int durationInMinutes; // Duration of the bus trip

    public BusTrip() {} // Constructor

    // Gets the transit details
    public TransitDetails getTransitDetails() {
        return transitDetails;
    }
    // Set transit details
    public void setTransitDetails(TransitDetails transitDetails) {
        this.transitDetails = transitDetails;
    }
    /**
     * Getters and setters
     */

    // Gets bus number
    public String getBusNumber() {
        return busNumber;
    }
    // Sets bus number
    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }
    // Gets bus name
    public String getBusName() {
        return busName;
    }
    // Sets bus name
    public void setBusName(String busName) {
        this.busName = busName;
    }
    // Gets departure time
    public Timestamp getDepartureTime() {
        return departureTime;
    }
    // Sets departure time
    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }
    // Gets arrivaltime
    public Timestamp getArrivalTime() {
        return arrivalTime;
    }
    // Sets arrival time
    public void setArrivalTime(Timestamp arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    // Gets duration in minutes
    public int getDurationInMinutes() {
        return durationInMinutes;
    }
    // Sets duration in minutes
    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }
}
