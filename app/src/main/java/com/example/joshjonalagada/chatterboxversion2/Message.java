package com.example.joshjonalagada.chatterboxversion2;

import android.util.Log;

import org.json.JSONException;
import org.json.simple.JSONObject;

public class Message {

    private User user;
    private String value;
    private String id;
    private int timestamp;

    public Message(JSONObject json) {
        id = (String) json.get("message_id");
        user = User.get((String) json.get("username"));
        value = (String) json.get("value");

        // TODO decode this string datetime into seconds
        timestamp = 0;
        // timestamp = (int) json.get("time");
    }

    public User getUser(){return user;}
    public String getValue() {return value;}
    public int getTimestamp() {return timestamp;}
}
