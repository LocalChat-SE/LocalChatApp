package com.example.joshjonalagada.chatterboxversion2;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import java.io.Serializable;

public class User implements Serializable {
    private static User loggedUser;
    private static Map<String, User> allUsers = new HashMap<>();

    private String username;

    private User(String username) {
        // this implementation enforces one instance of a given user
        this.username = username;
        allUsers.put(username, this);
    }

    // get arbitrary user
    public static User get(String username) {
        User user = allUsers.get(username);
        if (user == null) return new User(username);
        return user;
    }

    // get logged in user
    public static void setLoggedUser(String username) {loggedUser = get(username);}
    public static User getLoggedUser() {return loggedUser;}

    public String getUsername() {return username;}
}
