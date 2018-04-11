package com.example.joshjonalagada.chatterboxversion2;

public class Globals {

    private static Globals instance = null;
    private Globals() {}

    public Globals getInstance() {
        if (instance == null) instance = new Globals();
        return instance;
    }

    private Chat[] chats;
    private User loggedUser;

    private Chat[] getChats() {return chats;}
    private User getLoggedUser() {return loggedUser;}
}
