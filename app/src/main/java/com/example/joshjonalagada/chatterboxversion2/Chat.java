package com.example.joshjonalagada.chatterboxversion2;

import java.io.Serializable;

/**
 * Created by Tayfun Nalbantoglu on 4/10/2018.
 */

public class Chat implements Serializable {
    String name;
    float lat;
    float lon;
    Enrolled [] enrollments;
    String description;
    String chatID;
    Message [] history;
    String getName(){return name;}
    float getLat(){return lat;}
    float getLon(){return lon;}
    Enrolled[] getEnrollments(){return enrollments;}
    String getDescription(){return description;}
    String getChatID(){return chatID;}
    Message[] getHistory(){return history;}
    void sendMessage(String message){

    }
    void update(){

    }
}
