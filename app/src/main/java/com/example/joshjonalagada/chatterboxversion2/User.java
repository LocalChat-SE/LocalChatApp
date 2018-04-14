package com.example.joshjonalagada.chatterboxversion2;

import java.util.HashMap;
import java.util.Map;

public class User {
    private static User loggedUser;
    private static Map<String, User> allUsers = new HashMap<>();

    // TODO: additional descriptive fields, can be handled via API calls
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
    public static void setLoggedUser(String username) {loggedUser = new User(username);}
    public static User getLoggedUser() {return loggedUser;}

    public String getUsername() {return username;}
}
