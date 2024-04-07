package Boundary;

import Entity.Event;
import Entity.Schedule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scraper {
    private final Map<String, Integer> monthConversion = makeMonthConversionHashMap();

    public Schedule scrapeSchedule(String url, int weeksAhead){
        Document document = fetchDocument(url);

        if (document == null) {
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

        for (Element row : document.select("table.schematabell tr")){
            HashMap<String, String> rooms = new HashMap<>();
            ArrayList<String> teachers = new ArrayList<>();

            // Week number and year
            if (!row.select("td.vecka.data").text().isEmpty()) {
                weekNum = row.select("td.vecka.data").text();
                String[] parts = weekNum.split(", ");
                year = parts[1];
            }

            if (!row.select("td.data.commonCell:nth-of-type(4)").text().isEmpty()){
                // Date
                if (!row.select("td.data.commonCell:nth-of-type(3)").text().isEmpty()) {
                    date = row.select("td.data.commonCell:nth-of-type(3)").text();
                }

                // Time
                time = row.select("td.data.commonCell:nth-of-type(4)").text();

                // Course name
                if (!row.select("td.commonCell:nth-of-type(5) a").isEmpty()){
                    courseName = row.select("td.commonCell:nth-of-type(5) a").getFirst().text();
                }

                // Teacher codes to names
                for (Element teacherCode : row.select("td.commonCell:nth-of-type(6) a")){
                    if (allTeacherNames.containsKey(teacherCode.text())){
                        teachers.add(allTeacherNames.get(teacherCode.text()));
                    }
                }

                // Room codes to room names and maze map links
                for (Element classRoom : row.select("td.commonCell:nth-of-type(7) a")){
                    rooms.put(classRoom.text(), allRooms.getOrDefault(classRoom.text(), ""));
                }

                // Description
                if (!row.select("td.data.commonCell:nth-of-type(9)").isEmpty()){
                    description = row.select("td.data.commonCell:nth-of-type(9)").getFirst().text();
                }

                // Last updated
                if (!row.select("td.data.commonCell:nth-of-type(10)").isEmpty()){
                    lastUpdated = row.select("td.data.commonCell:nth-of-type(10)").getFirst().text();

                }

                // Convert the year, date and time to timestamps
                Timestamp[] timestamps = getTimeStamps(year, date, time);

                // Add the event to the list

                // if timestamps[1] is not more than two weeks in the future add the event
                if(timestamps[1].getTime() < timestampThreshold.getTime()){
                    events.add(new Event(  courseName,  timestamps[0],  timestamps[1],  calculateDuration(timestamps),  rooms,  teachers,  description, convertLastUpdatedToDate(lastUpdated)));
                }

            }
        }
        return new Schedule(url, events);
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



    public Timestamp[] getTimeStamps1(String year, String dayAndMonth, String startAndEndTime) {
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

        // Convert LocalDateTime to Instant
        Instant startInstant = startDateTime.toInstant(OffsetDateTime.now().getOffset());
        Instant endInstant = endDateTime.toInstant(OffsetDateTime.now().getOffset());

        // Convert Instant to Timestamp
        Timestamp startTimestamp = Timestamp.from(startInstant);
        Timestamp endTimestamp = Timestamp.from(endInstant);

        return new Timestamp[]{startTimestamp, endTimestamp};
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
}
