package com.wahidassistant.component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.TimeZone;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;
import com.wahidassistant.model.BikeEvent;
import com.wahidassistant.model.BusEvent;
import com.wahidassistant.model.Event;


public class TravelComponent {

    private final GeoApiContext context;

    String apiKey = "AIzaSyANvrEPWoFWNrnB4rHNBhEVy9Wgs43nc80";

    public TravelComponent() {
        context = new GeoApiContext.Builder().apiKey(apiKey).build();
        //getBusInfo("Båtbyggaregatan 32, 21642, Sweden", "Nordenskiöldsgatan 4, Malmö, Sweden");
        //getBikeInfo("Båtbyggaregatan 32, 21642, Sweden", "Nordenskiöldsgatan 4, Malmö, Sweden");
    }
/*
    public Event getBikeInfo(String originAddress, String destinationAddress, Date preferredArrivalTime, Date departureTime) {
        Event bikeEvent = null;
        try {
            if (preferredArrivalTime != null){
                DirectionsResult directions = DirectionsApi.getDirections(context, originAddress, destinationAddress)
                        .mode(TravelMode.BICYCLING)
                        .arrivalTime(preferredArrivalTime.toInstant())
                        .await();
                for (DirectionsStep step : directions.routes[0].legs[0].steps){
                    if (step.travelMode.equals(TravelMode.BICYCLING)){
                        Date departureTimeDirections = Date.from(step.transitDetails.departureTime.toInstant());
                        Date arrivalTime = Date.from(step.transitDetails.arrivalTime.toInstant());
                        String duration = String.valueOf(Duration.between(step.transitDetails.departureTime, step.transitDetails.arrivalTime).toMinutes());
                        bikeEvent = new BikeEvent(departureTimeDirections, arrivalTime,duration);
                    }
                }

                return bikeEvent;
                //System.out.println("Time to bike: " + directions.routes[0].legs[0].duration.inSeconds / 60 + " minutes");
            } else {
                DirectionsResult directions = DirectionsApi.getDirections(context, destinationAddress, originAddress)
                        .mode(TravelMode.BICYCLING)
                        .arrivalTime(departureTime.toInstant())
                        .await();
                for (DirectionsStep step : directions.routes[0].legs[0].steps){
                    if (step.travelMode.equals(TravelMode.BICYCLING)){
                        Date departureTimeDirections = Date.from(step.transitDetails.departureTime.toInstant());
                        Date arrivalTime = Date.from(step.transitDetails.arrivalTime.toInstant());
                        String duration = String.valueOf(Duration.between(step.transitDetails.departureTime, step.transitDetails.arrivalTime).toMinutes());
                        bikeEvent = new BikeEvent(departureTimeDirections, arrivalTime,duration);
                    }
                }
                return bikeEvent;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Event getBusInfo (String originAddress, String destinationAddress, Date preferredArrivalTime, Date departureTime) {
        Event busEvent = null;
        try {
            if (preferredArrivalTime != null){
                DirectionsResult directions = DirectionsApi.getDirections(context, originAddress, destinationAddress)
                        .mode(TravelMode.TRANSIT)
                        .arrivalTime(preferredArrivalTime.toInstant())
                        .await();

                for(DirectionsStep step : directions.routes[0].legs[0].steps) {
                    if (step.travelMode.equals(TravelMode.TRANSIT)) {
                        System.out.println("Transit");
                        String busNr = step.transitDetails.line.shortName;
                        String busName = step.transitDetails.headsign;
                        Date departureTimeDirections = Date.from(step.transitDetails.departureTime.toInstant());
                        Date arrivalTime = Date.from(step.transitDetails.arrivalTime.toInstant());
                        String duration = String.valueOf(Duration.between(step.transitDetails.departureTime, step.transitDetails.arrivalTime).toMinutes());
                        String origin = step.transitDetails.departureStop.name;
                        String destination = step.transitDetails.arrivalStop.name;
                        busEvent = new BusEvent(busNr, busName, departureTimeDirections, arrivalTime, duration, origin, destination, 0);
                    }
                }
                return busEvent;
            } else {
                DirectionsResult directions = DirectionsApi.getDirections(context, destinationAddress, originAddress)
                        .mode(TravelMode.TRANSIT)
                        .arrivalTime(departureTime.toInstant())
                        .await();

                for(DirectionsStep step : directions.routes[0].legs[0].steps) {
                    if (step.travelMode.equals(TravelMode.TRANSIT)) {
                        System.out.println("Transit");
                        String busNr = step.transitDetails.line.shortName;
                        String busName = step.transitDetails.headsign;
                        Date departureTimeDirections = Date.from(step.transitDetails.departureTime.toInstant());
                        Date arrivalTime = Date.from(step.transitDetails.arrivalTime.toInstant());
                        String duration = String.valueOf(Duration.between(step.transitDetails.departureTime, step.transitDetails.arrivalTime).toMinutes());
                        String origin = step.transitDetails.departureStop.name;
                        String destination = step.transitDetails.arrivalStop.name;
                        busEvent = new BusEvent(busNr, busName, departureTimeDirections, arrivalTime, duration, origin, destination, 0);
                    }
                }
                return busEvent;
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

 */
}
