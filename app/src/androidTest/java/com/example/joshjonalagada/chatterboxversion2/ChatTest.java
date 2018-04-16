package com.example.joshjonalagada.chatterboxversion2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

// name
// description
// longitude
// enrollment quantity
// first enrollment username
// first enrollment moderatorship
// first enrollment banned
// message quantity
// first message sender
// first message time

public class ChatTest {

    private ArrayList<String> sampleStrings = new ArrayList<>();
    private ArrayList<List<String>> sampleOracles = new ArrayList<>();

    public ChatTest() {
        sampleStrings.add("{\"description\":\"Initial Description\",\"longitude\":0.0,\"enrollments\":[{\"username\":\"Username\",\"moderator\":true,\"banned\":false}],\"start_date\":\"2018-04-16 03:55:34.567798\",\"latitude\":0.0,\"name\":\"Initial Chat\",\"messages\":[{\"value\":\"test message\",\"username\":\"Username\",\"time\":\"2018-04-16 03:55:34.568616\",\"id\":\"03d99a2a-412a-11e8-bdc4-7a727de2607c\"}]}");
        sampleOracles.add(Arrays.asList("Initial Chat", "Initial Description", "0.0", "1", "Username", "true", "false", "1", "Username", "Mon Apr 16 03:55:34 GMT+00:00 2018"));
    }

    // the constructor is empty, save for a call to update(). Using the constructor to test
    @Test
    public void update() throws Exception {

        for (int i=0; i < sampleStrings.size(); i++) {
            final JSONObject jsonTest = (JSONObject) new JSONParser().parse(sampleStrings.get(i));
            Chat chat = new Chat(jsonTest);
            List<String> oracle = sampleOracles.get(i);

            // Test chat construction
            if (!chat.getName().equals(oracle.get(0))) fail("NAME");
            if (!chat.getDescription().equals(oracle.get(1))) fail("DESCRIPTION");
            if (!String.valueOf(chat.getLon()).equals(oracle.get(2))) fail("LONGITUDE");

            // Test enrollment and user construction
            ArrayList<Enrolled> enrollments = chat.getEnrollments();
            if (!String.valueOf(enrollments.size()).equals(oracle.get(3))) fail("NUMBER OF ENROLLS");
            if (enrollments.size() != 0) {
                if (!(String.valueOf(enrollments.get(0).getUser().getUsername()).equals(oracle.get(4)))) fail("FIRST ENROLL USERNAME");
                if (!(String.valueOf(enrollments.get(0).getIsModerator()).equals(oracle.get(5)))) fail("FIRST ENROLL MODERATORSHIP");
                if (!(String.valueOf(enrollments.get(0).getIsBanned()).equals(oracle.get(6)))) fail("FIRST ENROLL BANNED");
            }

            // Test message construction
            ArrayList<Message> messages = chat.getHistory();
            if (!(String.valueOf(messages.size()).equals(oracle.get(7)))) fail("NUMER OF MESSAGES");
            if (messages.size() != 0) {
                if (!(String.valueOf(messages.get(0).getUser().getUsername()).equals(oracle.get(8)))) fail("FIRST MESSAGE USERNAME");
                if (!(String.valueOf(messages.get(0).getTimestamp()).equals(oracle.get(9)))) fail("FIRST MESSAGE TIMESTAMP");
            }
        }
    }
}