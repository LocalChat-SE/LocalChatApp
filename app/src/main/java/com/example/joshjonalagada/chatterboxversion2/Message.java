package com.example.joshjonalagada.chatterboxversion2;
import android.util.Log;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {

    private User user;
    private String value;
    private String id;
    private Date timestamp;

    public Message(JSONObject json) {
        id = (String) json.get("id");
        user = User.get((String) json.get("username"));
        value = (String) json.get("value");

        // timestamps are returned, but not used.
        // if message orders get jumbled, then these timestamps could be used to enforce correctness
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
            timestamp = format.parse((String) json.get("time"));
        } catch (ParseException exc) {
            Log.d("Message", "Could not parse time.");
        }
    }

    public String getID(){return id;}
    public User getUser(){return user;}
    public String getValue() {return value;}
    public Date getTimestamp() {return timestamp;}
}
