package com.example.joshjonalagada.chatterboxversion2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LoginGUI extends AppCompatActivity { //this name may need to change to LoginController

    User loggedInUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) //this function is called first, like a constructor
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_gui);
        Button submitButton = findViewById(R.id.submitButton); //getting the submit button for he event handler
        submitButton.setOnClickListener(new View.OnClickListener() { //this is the onClick listener for submit, it will validate the user

            @Override
            public void onClick(View v) {
                validateUser();
            }
        });
        Button registerButton = findViewById(R.id.registerButton); //getting the register button for the event handler
        registerButton.setOnClickListener(new View.OnClickListener() { //this is the onClick listener for submit, it will validate the user

            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    public void createUser()
    {
        // just an example for parsing an arbitrary text string
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse("{\"description\": \"chats fetched\", \"status\": true, \"data\": [[\"e2f9282c-3dcd-11e8-bdc4-7a727de2607c\", \"test_chat2\", \"This is a test chat, for test chatting!\", 33.987286, -6.748288, 90.00511231161964]]}");
            Log.d("TAG", String.valueOf(json.get("data").getClass()));

//            Should be able to create instances of a class using a JSONObject!
//            We can use the creator pattern, where the Chat class is the creator of enrollments and messages
//            Remember that the chats are initially created without data-- the data is loaded in when you open the chat
//            new Chat(json.get("data"));
        } catch (ParseException e) {
            Log.d("LoginGUI", "Could not parse!");
        }
    }

    public void validateUser()
    {
        // callback for processing the response from the API
        class LoginListener implements ResponseListener {
            public void getResult(JSONObject response) {
                Log.d("LoginController", "In login callback");
                Log.d("LoginController", String.valueOf(response));

                openChatList();
            }
        }

        APIManager.getInstance().getUser(new LoginListener(),"test_user", "test_password");
    }
    public void openChatList(){
        startActivity(new Intent(LoginGUI.this, ChatListController.class));
    }

}
