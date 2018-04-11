package com.example.joshjonalagada.chatterboxversion2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    }
    public void validateUser()
    {
        APIManager.getInstance(this).loginUser("test_user", "test_password");
        
        openChatList();
    }
    public void openChatList(){
        startActivity(new Intent(LoginGUI.this, ChatListController.class));
    }

}
