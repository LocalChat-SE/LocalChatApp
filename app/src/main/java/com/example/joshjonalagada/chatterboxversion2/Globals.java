package com.example.joshjonalagada.chatterboxversion2;

public class Globals {

    private static Globals instance = null;

    private Globals() {}

    public static Globals getInstance() {
        if (instance == null) instance = new Globals();
        return instance;
    }

    private User loggedUser;

    public User getLoggedUser() {return loggedUser;}
}
