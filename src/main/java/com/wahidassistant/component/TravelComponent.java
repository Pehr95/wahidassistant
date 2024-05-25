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
import com.wahidassistant.model.BusEvent;


public class TravelComponent {

    private final GeoApiContext context;

    String apiKey = "AIzaSyANvrEPWoFWNrnB4rHNBhEVy9Wgs43nc80";

    public TravelComponent() {
        context = new GeoApiContext.Builder().apiKey(apiKey).build();
        getBusInfo("Båtbyggaregatan 32, 21642, Sweden", "Nordenskiöldsgatan 4, Malmö, Sweden");
        getBikeInfo("Båtbyggaregatan 32, 21642, Sweden", "Nordenskiöldsgatan 4, Malmö, Sweden");
    }

    public void getBikeInfo(String originAddress, String destinationAddress) {
        try {
            DirectionsResult directions = DirectionsApi.getDirections(context, originAddress, destinationAddress)
                    .mode(TravelMode.BICYCLING)
                    .await();
            System.out.println("Time to bike: " + directions.routes[0].legs[0].duration.inSeconds / 60 + " minutes");
        } catch (Exception e) {
            e.printStackTrace();
        }
        }


    // "Censorsgatan 3, 21550, Sweden"

    public void getBusInfo (String originAddress, String destinationAddress) {
        try {
            Date testDate = makeDate();
            DirectionsResult directions = DirectionsApi.getDirections(context, originAddress, destinationAddress)
                    .mode(TravelMode.TRANSIT)
                    .arrivalTime(testDate.toInstant())
                    .await();

            for(DirectionsStep step : directions.routes[0].legs[0].steps) {
                if (step.travelMode.equals(TravelMode.TRANSIT)) {
                    System.out.println("Transit");
                    String busNr = step.transitDetails.line.shortName;
                    String busName = step.transitDetails.headsign;
                    Date departureTime = Date.from(step.transitDetails.departureTime.toInstant());
                    Date arrivalTime = Date.from(step.transitDetails.arrivalTime.toInstant());
                    String duration = String.valueOf(Duration.between(step.transitDetails.departureTime, step.transitDetails.arrivalTime).toMinutes());
                    String origin = step.transitDetails.departureStop.name;
                    String destination = step.transitDetails.arrivalStop.name;
                    BusEvent busEvent = new BusEvent(busNr, busName, departureTime, arrivalTime, duration, origin, destination, 0);
                    System.out.println(busEvent);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Date makeDate() {
        String dateString = "2024-05-16T06:15:00.000+00:00";
        Date date = null;

        try {
            // Define the date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

            // Set the time zone to UTC
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Parse the string into a Date object
            date = sdf.parse(dateString);

            // Print the Date object
            System.out.println("Date: " + date);
            date.setTime(date.getTime() - 5*60*1000);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }

        return date;
    }

    public static void main(String[] args) {
        TravelComponent travelComponent = new TravelComponent();

    }
}
