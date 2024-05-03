package com.wahidassistant.component;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import com.wahidassistant.model.BusEvent;
import com.wahidassistant.model.BusTrip;
import com.wahidassistant.model.Event;
import com.wahidassistant.model.User;
import org.jetbrains.annotations.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TravelService {
    private final GeoApiContext context;

    String apiKey = "AIzaSyANvrEPWoFWNrnB4rHNBhEVy9Wgs43nc80";

    public TravelService() {
        context = new GeoApiContext.Builder().apiKey(apiKey).build();
    }

    public int transportTime (TravelMode type, String originAddress, String destinationAddress) {
        int transportTimeInMinutes = 0;
        try {
            DirectionsResult directions = DirectionsApi.getDirections(context, originAddress, destinationAddress)
                    .mode(type)
                    .await();
            transportTimeInMinutes = (int) (directions.routes[0].legs[0].duration.inSeconds / 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transportTimeInMinutes;
    }

    public Timestamp busArrivalTime(TravelMode type, String originAddress, String destinationAddress, Instant arrivalTime) {
        Timestamp busArrivalTime = null;
        try {
            DirectionsResult directions = DirectionsApi.getDirections(context, originAddress, destinationAddress)
                    .mode(type)
                    .arrivalTime(arrivalTime)
                    .await();
            long arrivalTimeMillis = directions.routes[0].legs[0].arrivalTime.getSecond() * 1000;
            busArrivalTime = new Timestamp(arrivalTimeMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return busArrivalTime;
    }

    public List<BusTrip> getBusTrips(String originAddress, String destinationAddress, Instant arrivalTime){
        List<BusTrip> busTrips = new ArrayList<>();
        try {
            DirectionsResult directions = DirectionsApi.getDirections(context, originAddress, destinationAddress)
                    .mode(TravelMode.TRANSIT)
                    .arrivalTime(arrivalTime)
                    .await();
            for (DirectionsRoute route : directions.routes) {
                for (DirectionsLeg leg : route.legs) {
                    for (DirectionsStep step : leg.steps) {
                        if (step.travelMode.equals(TravelMode.TRANSIT)) {
                            TransitDetails transitDetails = step.transitDetails;
                            if (transitDetails != null) {
                                BusTrip busTrip = getBusTrip(transitDetails);
                                busTrips.add(busTrip);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return busTrips;
    }

    @NotNull
    private static BusTrip getBusTrip(TransitDetails transitDetails) {
        BusTrip busTrip = new BusTrip();
        busTrip.setBusNumber(transitDetails.line.shortName);
        busTrip.setBusName(transitDetails.line.name);
        busTrip.setDepartureTime(new Timestamp(transitDetails.departureTime.getSecond() * 1000));
        busTrip.setArrivalTime(new Timestamp(transitDetails.arrivalTime.getSecond() * 1000));
        int durationInMinutes = transitDetails.arrivalTime.getMinute() - transitDetails.departureTime.getMinute();
        busTrip.setDurationInMinutes(durationInMinutes);
        return busTrip;
    }

    public int getTotalTravelTime(List<BusTrip> busTrips){
        int totalTravelTime = 0;
        for(BusTrip busTrip : busTrips){
            totalTravelTime += busTrip.getDurationInMinutes();
        }
        return totalTravelTime;
    }

    //todo: Få ut första dagens event, få ut eventets tid, skapa bus event och lägg in i mongodb som BusEvent innan eventet i personliga schedule.
    /*
    public BusEvent busEventbeforeEvent(Event event, User user){

        user.getCustomEvents.getfirst

        BusEvent busEvent = new BusEvent();
        return busEvent;
    }

     */
}
