package Controller;

import Entity.Event;
import Entity.Schedule;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseController {
    private MongoClient mongoClient;
    private MongoDatabase database;
    public DatabaseController() {
        getMongoClientAndDatabase();
    }
    public void getMongoClientAndDatabase() {
        // MongoDB deployment's connection string
        String uri = "mongodb+srv://pehrnorten:lSTVugtf8zul0Jy5@wahidassistant.os9sscp.mongodb.net/";
        try {
            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase("WahidAssistant");
            System.out.println("Connected to database");
        } catch (Exception e) {
            disconnect();
            e.printStackTrace();
        }
    }
    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public void insertSchedule(Schedule schedule) {
        try {
            MongoCollection<Document> collection = database.getCollection("Schedule");
            Document document = new Document();
            document.put("url", schedule.getUrl());
            ArrayList<Document> events = new ArrayList<>();
            for (Event event : schedule.getEvents()) {
                Document eventDocument = new Document();
                eventDocument.put("startTime", event.getStartTime());
                eventDocument.put("endTime", event.getEndTime());
                eventDocument.put("courseName", event.getCourseName());
                eventDocument.put("teachers", event.getTeachers());
                eventDocument.put("rooms", event.getRooms());
                eventDocument.put("description", event.getDescription());
                eventDocument.put("lastUpdated", event.getLastUpdated());
                events.add(eventDocument);
            }
            document.put("events", events);
            collection.insertOne(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Schedule getScheduleFromDatabase(String url) {
        try {
            MongoCollection<Document> collection = database.getCollection("Schedule");
            Document query = new Document();
            query.put("url", url);
            Document result = collection.find(query).first();
            if (result != null) {
                ArrayList<Event> events = new ArrayList<>();
                for (Document eventDocument : (ArrayList<Document>) result.get("events")) {
                    Event event = new Event(
                            eventDocument.getString("courseName"),
                            eventDocument.getDate("startTime"),
                            eventDocument.getDate("endTime"),
                            0,
                            convertDocumentToHashMap(eventDocument),
                            (ArrayList<String>) eventDocument.get("teachers"),
                            eventDocument.getString("description"),
                            eventDocument.getDate("lastUpdated")
                    );
                    events.add(event);
                }
                return new Schedule(url, events);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean checkIfScheduleExists(String url) {
        try {
            MongoCollection<Document> collection = database.getCollection("Schedule");
            Document query = new Document();
            query.put("url", url);
            Document result = collection.find(query).first();
            if (result != null) {
                System.out.println("Schedule exists and have the document-id: " + result.get("_id"));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private HashMap<String, String> convertDocumentToHashMap(Document document) {
        // Convert MongoDB Document to HashMap
        HashMap<String, String> hashMap = new HashMap<>();
        for (HashMap.Entry<String, Object> entry : document.entrySet()) {
            hashMap.put(entry.getKey(), entry.getValue().toString());
        }
        return hashMap;
    }
}
