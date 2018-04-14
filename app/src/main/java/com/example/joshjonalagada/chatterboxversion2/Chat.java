package com.example.joshjonalagada.chatterboxversion2;

import android.util.Log;

import org.json.simple.JSONObject;

import java.io.Serializable;

public class Chat implements Serializable {
    private String name;
    private String description;
    private double lat;
    private double lon;
    private double distance;

    private Enrolled [] enrollments;
    private String chatID;
    private Message [] history;

    public Chat(JSONObject json) {
        Log.d("Chat", json.toString());
        if (json.containsKey("name")) name = (String) json.get("name");
        if (json.containsKey("description")) description = (String) json.get("description");
        if (json.containsKey("latitude")) lat = (double) json.get("latitude");
        if (json.containsKey("longitude")) lon = (double) json.get("longitude");
        if (json.containsKey("distance")) distance = (double) json.get("distance");
        if (json.containsKey("chat_id")) chatID = (String) json.get("chat_id");

        // start time is not actually displayed in the UI, ignore it in the json
        // if (json.containsKey("start_time")) start_time = (String) json.get("start_time");


        // TODO: implement user, messages and enrollments constructors
        if (json.containsKey("users")) {
            json.get("users");
        }

        if (json.containsKey("messages")) {
            json.get("messages");
        }

        if (json.containsKey("enrollments")) {
            json.get("enrollments");
        }
    }

    public String getName(){return name;}
    public String getDescription(){return description;}
    public double getLat(){return lat;}
    public double getLon(){return lon;}
    public double getDistance(){return distance;}

    public Enrolled[] getEnrollments(){return enrollments;}
    public String getChatID(){return chatID;}
    public Message[] getHistory(){return history;}

    public void sendMessage(String message){

    }

    void update(){

    }
}
