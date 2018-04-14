package com.example.joshjonalagada.chatterboxversion2;

import org.json.simple.JSONObject;

public class Enrolled {
    private User user;
    private boolean isModerator;
    private boolean isBanned;

    public Enrolled(JSONObject json) {
        user = User.get((String) json.get("username"));
        isModerator = (Boolean) json.get("moderator");
        isBanned = (Boolean) json.get("banned");
    }

    public User getUser() {return user;}
    public boolean getIsBanned() {return isBanned;}
    public boolean getIsModerator() {return isModerator;}
}
