package com.example.joshjonalagada.chatterboxversion2;

import java.util.HashMap;
import java.util.Map;

public class User {
    private static Map<String, User> allUsers = new HashMap<>();

    // TODO: additional descriptive fields, can be handled via API calls
    private String username;

    private User(String username) {
        // this implementation enforces one instance of a given user
        this.username = username;
        allUsers.put(username, this);
    }

    public static User get(String username) {
        User user = allUsers.get(username);
        if (user == null) return new User(username);
        return user;
    }

    public String getUsername()
    {
        return username;
    }
}
