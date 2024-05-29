package com.wahidassistant.component;

import java.io.IOException;
import java.net.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// It's not implemented


// This class is responsible for fetching and processing weather data from an SMHI API.

public class WeatherService {
    String metObsAPI = "https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/13.095703/lat/55.519302/data.json";




    // This method is the entry point for fetching the weather data.

    public void startHere() throws MalformedURLException, IOException {
        URL url = new URL(metObsAPI);



        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try {
            getPeriodNames("2", ",");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // This method reads a JSON object from a given URL.

    public  JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        String text = readStringFromUrl(url);
        return new JSONObject(text);
    }

    // This method reads a string from a given URL.
    public  String readStringFromUrl(String url) throws IOException {

        InputStream inputStream = new URL(url).openStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }
            return stringBuilder.toString();
        } finally {
            inputStream.close();
        }
    }

    // This method fetches and processes the weather data for different periods.

    private String getPeriodNames(String parameterKey, String stationKey) throws IOException, JSONException {


        String paramName = null;
        ArrayList <List<String>> forcast = new ArrayList<>();
        ArrayList <String> timestamp = new ArrayList<>();

        JSONObject periodsObject = readJsonFromUrl(metObsAPI);
        JSONArray timeSeries = periodsObject.getJSONArray("timeSeries");

        for (int i = 0; i < timeSeries.length(); i++) {
            JSONObject timePeriod = timeSeries.getJSONObject(i);
            String validTime = timePeriod.getString("validTime");
            String patter = "(\\d{4}-\\d{2}-\\d{2})T(\\d{2})";
            Pattern pattern = Pattern.compile(patter);
            Matcher matcher = pattern.matcher(validTime);
            if (matcher.find()) {
                String date = matcher.group(1);
                String time = matcher.group(2);
                validTime = date + " " + time;
            }
            JSONArray parameters = timePeriod.getJSONArray("parameters");
            for (int j = 0; j < parameters.length(); j++) {
                JSONObject parameter = parameters.getJSONObject(j);
                 paramName = parameter.getString("name");
                if (paramName.equals("Wsymb2")) {
                    int wsymb2Value = parameter.getJSONArray("values").getInt(0);
                    timestamp.add(validTime + " " + valuesToWeather(wsymb2Value));
                }
            }

        }






         for(String list: timestamp){
            // System.out.println(list);
             //String a = "2024-03-28T15:00:00";
             String patter = "(\\d{4}-\\d{2}-\\d{2})T(\\d{2})";
             Pattern pattern = Pattern.compile(patter);
             Matcher matcher = pattern.matcher(list);
             if (matcher.find()) {
                 String date = matcher.group(1);
                 String time = matcher.group(2);


                 System.out.println(date + " " + time );
                 timestamp.add(date + " " + time + " " + valuesToWeather(1) ); //todo fixa detta
             }



        }
        for(int o = 0; o< timestamp.size(); o++){
            System.out.println(timestamp.get(o));

        }

        return paramName;
    }

    // This method converts a weather code to a human-readable weather description.
    public  String valuesToWeather(int value){
        String weather = "";
        switch (value){
            case 1: weather = "Clear sky";
                break;
            case 2:	weather = "Nearly clear sky";
                break;
            case 3:	weather = "Variable cloudiness";
                break;
            case 4:	weather = "Halfclear sky";
                break;
            case 5:	weather = "Cloudy sky";
                break;
            case 6:	weather = "Overcast";
                break;
            case 7:	weather = "Fog";
                break;
            case 8:	weather = "Light rain showers";
                break;
            case 9:	weather = "Moderate rain showers";
                break;
            case 10:weather = "Heavy rain showers";
                break;
            case 11:weather = "Thunderstorm";
                break;
            case 12:weather = "Light sleet showers";
                break;
            case 13:weather = "Moderate sleet showers";
                break;
            case 14:weather = "Heavy sleet showers";
                break;
            case 15:weather = "Light snow showers";
                break;
            case 16:weather = "Moderate snow showers";
                break;
            case 17:weather = "Heavy snow showers";
                break;
            case 18:weather = "Light rain";
                break;
            case 19:weather = "Moderate rain";
                break;
            case 20:weather = "Heavy rain";
                break;
            case 21:weather = "Thunder";
                break;
            case 22:weather = "Light sleet";
                break;
            case 23:weather = "Moderate sleet";
                break;
            case 24:weather = "Heavy sleet";
                break;
            case 25:weather = "Light snowfall";
                break;
            case 26:weather = "Moderate snowfall";
                break;
            case 27:weather = "Heavy snowfall";
                break;
        }


        return weather;

   }


}
