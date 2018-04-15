package com.example.joshjonalagada.chatterboxversion2;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Serializable {
    private String chatID;

    private String name;
    private String description;
    private double lat;
    private double lon;
    private double distance;

    private ArrayList<Enrolled> enrollments;
    private ArrayList<Message> history = new ArrayList<>();

    private static Chat currentChat;
    public static Chat getCurrentChat() {
        return currentChat;
    }
    public static void setCurrentChat(Chat chat) {
        currentChat = chat;
    }

    public Chat(JSONObject json) {
        update(json);
    }

    public void update(JSONObject json) {

        // Log.d("Chat", json.toString());
        if (json.containsKey("name")) name = (String) json.get("name");
        if (json.containsKey("description")) description = (String) json.get("description");
        if (json.containsKey("latitude")) lat = (double) json.get("latitude");
        if (json.containsKey("longitude")) lon = (double) json.get("longitude");
        if (json.containsKey("distance")) distance = (double) json.get("distance");
        if (json.containsKey("chat_id")) chatID = (String) json.get("chat_id");

        // start time is not actually displayed in the UI, ignore it in the json
        // if (json.containsKey("start_time")) start_time = (String) json.get("start_time");

        if (json.containsKey("messages")) {
            JSONArray jsonMessages = (JSONArray) json.get("messages");
            for (int i = 0; i < jsonMessages.size(); i++) {
                Message newMessage = new Message((JSONObject) jsonMessages.get(i));
                for (Message msg : history) {
                    if (msg.getID().equals(newMessage.getID())) return;
                }
                history.add(newMessage);
            }
        }

        if (json.containsKey("enrollments")) {
            JSONArray jsonEnrollments = (JSONArray) json.get("enrollments");

            enrollments = new ArrayList<>();
            // reconstruct enrollments list
            for (int i = 0; i < jsonEnrollments.size(); i++) {
                enrollments.add(new Enrolled((JSONObject) jsonEnrollments.get(i)));
            }
        }
    }

    public String getName(){return name;}
    public String getDescription(){return description;}
    public double getLat(){return lat;}
    public double getLon(){return lon;}

    public Enrolled getEnrollment(User user){
        for (Enrolled enrollment : enrollments) {
            if (enrollment.getUser().getUsername().equals(user.getUsername())) return enrollment;
        }
        return null;
    }

    public String getChatID(){return chatID;}
    public double getDistance(){return distance;}

    public ArrayList<Enrolled> getEnrollments(){return enrollments;}
    public ArrayList<Message> getHistory(){return history;}
}
