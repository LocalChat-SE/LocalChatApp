package com.example.joshjonalagada.chatterboxversion2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.simple.JSONObject;

public class LoginController extends AppCompatActivity { //this name may need to change to LoginController

    EditText usernameField;
    EditText passwordField;
    TextView credentialsPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) //this function is called first, like a constructor
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_gui);

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        credentialsPrompt = findViewById(R.id.credentialsPrompt);

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

    public void createUser() {
        APIManager manager = APIManager.getInstance();

        // callback for processing the response from the API
        class CreateUserListener implements ResponseListener {
            public void getResult(final JSONObject response) {

                if ((Boolean) response.get("status")) {
                    validateUser();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            credentialsPrompt.setText((String) response.get("description"));
                        }
                    });
                }
            }
        }

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        manager.setUser(new CreateUserListener(),username, password);
    }

    public void validateUser() {
        // callback for processing the response from the API
        class LoginListener implements ResponseListener {
            public void getResult(final JSONObject response) {
                if ((Boolean) response.get("status")) {
                    openChatList();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            credentialsPrompt.setText((String) response.get("description"));
                        }
                    });
                }
            }
        }

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        APIManager.getInstance().getUser(new LoginListener(), username, password);
    }

    public void openChatList(){
        startActivity(new Intent(LoginController.this, ChatListController.class));
    }

}
