package com.wahidassistant.component;

import com.wahidassistant.model.Event;
import com.wahidassistant.model.Schedule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebScraper {
    private final Map<String, Integer> monthConversion = makeMonthConversionHashMap();
    private HashMap<String, String> columnNumbers = new HashMap<>();

    public Schedule scrapeSchedule(String url) {
        int weeksAhead = 2; // How many weeks ahead to scrape
        // Todo: felhantering. Kolla om dokumentet har rätt typ av format samt att datum formattering är korrekt.

        Document document = checkIfUrlIsValid(url);

        if (document == null){
            return null;
        }

        ArrayList<Event> events = new ArrayList<>();
        Timestamp timestampThreshold = getMondayTimestampXWeeksAhead(weeksAhead);

        // Scrape the rooms and teacher names
        HashMap<String, String> allRooms = scrapeRooms(document);
        HashMap<String, String> allTeacherNames = scrapeTeacherNames(document);

        // Variables to store the data while looping through the table
        String date = "";
        String time;
        String courseName = "";
        String description = "";
        String lastUpdated = "";
        String weekNum = "";
        String year = "";


        // Variables to store html column query
        String dateColumnQuery1 = "td.data.commonCell:nth-of-type(" + (Integer.parseInt(columnNumbers.get("Datum"))+1) +")";
        String dateColumnQuery2 = "td.data.commonCell:nth-of-type(" + columnNumbers.get("Datum") +")";
        String timeColumnQuery = "td.data.commonCell:nth-of-type(" + columnNumbers.get("Start-Slut") + ")";
        String courseNameColumnQuery = "td.commonCell:nth-of-type(" + columnNumbers.get("Kurs.grp") + ") a";
        String teacherCodesColumnQuery = "td.commonCell:nth-of-type(" + columnNumbers.get("Sign") + ") a";
        String roomCodesColumnQuery = "td.commonCell:nth-of-type(" + columnNumbers.get("Lokal") + ") a";
        String descriptionColumnQuery = "td.data.commonCell:nth-of-type(" + columnNumbers.get("Moment") + ")";
        String lastUpdatedColumnQuery = "td.data.commonCell:nth-of-type(" + columnNumbers.get("Uppdat.") + ")";


        for (Element row : document.select("table.schematabell tr")){
            HashMap<String, String> rooms = new HashMap<>();
            ArrayList<String> teachers = new ArrayList<>();

            // Week number and year
            if (!row.select("td.vecka.data").text().isEmpty()) {
                weekNum = row.select("td.vecka.data").text();
                String[] parts = weekNum.split(", ");
                year = parts[1];
            }

            if (!row.select(dateColumnQuery1).text().isEmpty()){
                // Date
                if (!row.select(dateColumnQuery2).text().isEmpty()) {
                    date = row.select(dateColumnQuery2).text();
                }

                // Time
                time = row.select(timeColumnQuery).text();

                // Course name
                if (!row.select(courseNameColumnQuery).isEmpty()){
                    courseName = extractCourseName(row.select(courseNameColumnQuery).get(0).text());
                }

                // Teacher codes to names
                for (Element teacherCode : row.select(teacherCodesColumnQuery)){
                    if (allTeacherNames.containsKey(teacherCode.text())){
                        teachers.add(allTeacherNames.get(teacherCode.text()));
                    }
                }

                // Room codes to room names and maze map links
                for (Element classRoom : row.select(roomCodesColumnQuery)){

                    rooms.put(classRoom.text(), trimLettersAtEndOfMazeMapUrl(allRooms.getOrDefault(classRoom.text(), "")));
                    //rooms.put(classRoom.text(), allRooms.getOrDefault(classRoom.text(), ""));
                }

                // Description
                if (!row.select(descriptionColumnQuery).isEmpty()){
                    description = row.select(descriptionColumnQuery).get(0).text();
                }

                // Last updated
                if (!row.select(lastUpdatedColumnQuery).isEmpty()){
                    lastUpdated = row.select(lastUpdatedColumnQuery).get(0).text();
                }

                // Convert the year, date and time to timestamps
                Timestamp[] timestamps = getTimeStamps(year, date, time);

                // Add the event to the list

                // if timestamps[1] is not more than two weeks in the future add the event
                if(timestamps[1].getTime() < timestampThreshold.getTime()){
                    events.add(new Event(courseName, timestamps[0], timestamps[1], calculateDuration(timestamps), rooms, teachers, description, convertLastUpdatedToDate(lastUpdated), false));
                }
            }
        }
        return new Schedule(url, events, new java.util.Date());
    }

    public String trimLettersAtEndOfMazeMapUrl(String input) {
        int length = input.length();
        StringBuilder stringBuilder = new StringBuilder(input);

        for (int i = length - 1; i >= 0; i--) {
            char c = stringBuilder.charAt(i);
            if (Character.isDigit(c)) {
                break;
            } else if (Character.isLetter(c)) {
                stringBuilder.deleteCharAt(i);
            }
        }

        return stringBuilder.toString();
    }

    public static String extractCourseName(String inputString) {
        String regex = "^(.*?), \\d+(?:\\.\\d+)? hp"; // This regex captures everything before " {digit} hp "
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return "No match found.";
        }
    }

    private Document fetchDocument(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String> scrapeRooms(Document document) {
        HashMap<String, String> roomsHashMap = new HashMap<>();
        String id;
        String mazeMapLink;

        for (Element anchorTag : document.select("div#forklaringstexter a")){
            if(anchorTag.attr("href").contains("mazemap")){
                id = anchorTag.text();
                mazeMapLink = anchorTag.attr("href");
                roomsHashMap.put(id, mazeMapLink);
            }
        }
        return roomsHashMap;
    }

    public HashMap<String, String> scrapeTeacherNames(Document document) {
        HashMap<String, String> teacherNamesHashMap = new HashMap<>();
        String sign;
        String firstName;
        String lastName;

        for (Element row : document.select("table.forklaringstabell tr.data-white")){
            sign = row.select(".closeLeftCell").text(); // First column
            firstName = row.select(".commoncell").get(0).text(); // Second column
            lastName = row.select(".commoncell").get(1).text(); // Third column
            teacherNamesHashMap.put(sign, firstName + " " + lastName);
        }
        return teacherNamesHashMap;
    }

    public Map<String, Integer> makeMonthConversionHashMap(){
        Map<String, Integer> map = new HashMap<>();
        map.put("jan", 1);
        map.put("feb", 2);
        map.put("mar", 3);
        map.put("apr", 4);
        map.put("maj", 5);
        map.put("jun", 6);
        map.put("jul", 7);
        map.put("aug", 8);
        map.put("sep", 9);
        map.put("okt", 10);
        map.put("nov", 11);
        map.put("dec", 12);
        return map;
    }

    public Timestamp[] getTimeStamps(String year, String dayAndMonth, String startAndEndTime){
        String[] parts = dayAndMonth.split(" ");
        int day = Integer.parseInt(parts[0]);
        int month = monthConversion.get(parts[1].toLowerCase());

        parts = startAndEndTime.split("-");
        String startTime = parts[0];
        String endTime = parts[1];
        String[] timeComponentsStartTime = startTime.split(":");
        String[] timeComponentsEndTime = endTime.split(":");
        int startHour = Integer.parseInt(timeComponentsStartTime[0]);
        int startMinute = Integer.parseInt(timeComponentsStartTime[1]);
        int endHour = Integer.parseInt(timeComponentsEndTime[0]);
        int endMinute = Integer.parseInt(timeComponentsEndTime[1]);

        // Parse the given date and time
        LocalDateTime startDateTime = LocalDateTime.of(Integer.parseInt(year), month, day, startHour, startMinute);
        LocalDateTime endDateTime = LocalDateTime.of(Integer.parseInt(year), month, day, endHour, endMinute);

        return new Timestamp[] {Timestamp.valueOf(startDateTime),Timestamp.valueOf(endDateTime)};
    }

    public int calculateDuration(Timestamp[] timestamps) {
        return (int) ((timestamps[1].getTime() - timestamps[0].getTime()) / (1000 * 60));
    }

    public Date convertLastUpdatedToDate(String lastUpdated) {
        LocalDate localDate = LocalDate.parse(lastUpdated, DateTimeFormatter.ISO_LOCAL_DATE);
        return Date.valueOf(localDate);
    }

    public Timestamp getMondayTimestampXWeeksAhead(int weeksAhead) {
        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Get the next Monday after 'weeksAhead' weeks
        LocalDate nextMonday = currentDate.plusWeeks(weeksAhead+1).with(DayOfWeek.MONDAY);

        // Convert to Timestamp
        return Timestamp.valueOf(nextMonday.atStartOfDay());
    }

    public Document checkIfUrlIsValid(String url) {

        if (!url.contains("https://")) {
            return null;
        }

        if (url.contains("d&intervallAntal=")){
            return null;
        }

        Document document = fetchDocument(url);
        System.out.println("Fetching document from: " + url);
        columnNumbers = new HashMap<>();
        String[] requiredColumns = {"Datum", "Start-Slut", "Kurs.grp", "Sign", "Lokal", "Moment", "Uppdat."};


        // Create HashMap with all column headers and their column numbers
        if (document != null) {
            int columnNumber = 2;
            for (Element row : document.select("table.schematabell tr")){
                if (!row.select("th.commonCell.header").isEmpty()) {
                    for (Element columnHeader : row.select("th.commonCell.header")) {
                        columnNumbers.put(columnHeader.text(), Integer.toString(columnNumber++));
                    }
                }
            }
            // Check if HashMap contains all requiredColumns
            for (String requiredColumn : requiredColumns) {
                if (!columnNumbers.containsKey(requiredColumn)){
                    System.out.println("URL is invalid since at least column \"" + requiredColumn + "\" is missing: ");
                    return null;
                }
            }
            System.out.println("All required columns are present");
            return document;
        }

        return null;
    }
}
