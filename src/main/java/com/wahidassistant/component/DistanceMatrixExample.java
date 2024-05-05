package com.wahidassistant.component;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.sql.Timestamp;

public class DistanceMatrixExample {

    String apiKey = "AIzaSyANvrEPWoFWNrnB4rHNBhEVy9Wgs43nc80";

    GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(apiKey)
            .build();

    public String currentLocation(){

        String responseBody = "";
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://www.googleapis.com/geolocation/v1/geolocate?key=" + apiKey);

        try {
            StringEntity requestBody = new StringEntity("{}");
            httpPost.setEntity(requestBody);

            HttpResponse response = httpClient.execute(httpPost);

            responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("Response: " + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    /*
    Method returns traveltime by specific transporttype from two addresses.

    public int transportTime(TravelMode type, String hemAddress, String destinationsAddress){
        //Malmö Uni = Neptuniplan 7, Malmö, Sverige
        Duration duration = new Duration();
        try {
            DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(context)
                    .origins("Censorsgatan 3C, Malmö, Sweden")
                    .destinations("Neptuniplan 7, Malmö, Sweden")
                    .mode(type)
                    .await();

            System.out.println("Avstånd: " + distanceMatrix.rows[0].elements[0].distance);

            duration = distanceMatrix.rows[0].elements[0].duration;

            System.out.println("Tid: " + duration);

// todo: fixa så att den får ut busstider och avgångstider samt ankomststider

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Math.ceilDiv(Math.toIntExact(duration.inSeconds), 60);
    }
     */
}
