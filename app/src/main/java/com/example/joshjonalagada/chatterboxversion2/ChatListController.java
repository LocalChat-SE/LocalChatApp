package com.example.joshjonalagada.chatterboxversion2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ChatListController extends AppCompatActivity {

    // turn this off to disable polling for groups
    volatile boolean updateThread = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_gui);

        Button createGroupButton = findViewById(R.id.createButton);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateThread = false;
                startActivity(new Intent(ChatListController.this, CreateGroupController.class));
            }
        });

        Button logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                    Log.d("ChatListController", response.toString());
                    if ((Boolean) response.get("status")) return;
                    Log.d("ChatListController", "unsuccessful");
                } catch (ParseException exc) {
                    Log.e("ChatListController", "Could not parse: " + responseData);
                }
            }
        };

        final Thread updateLoop = new Thread(new Runnable() {
            public void run() {
                while (updateThread) {
                    try {
                        Thread.sleep(5000);
                        // TODO location is hardcoded
                        APIManager.getInstance().getChats(ChatListController.this, listener, 32.987, -96.747);

                    } catch (InterruptedException e) {
                        Log.d("ChatListController", "Interrupted update loop");
                        return;
                    }
                }
                updateThread = true;
            }
        });

        updateLoop.start();
    }

    public void logoutUser() {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                    if ((Boolean) response.get("status")) return;
                    Log.d("ChatListController", "Error logging out. Continuing regardless.");
                } catch (ParseException exc) {
                    Log.e("ChatListController", "Could not parse: " + responseData);
                }
            }
        };

        updateThread = false;
        APIManager.getInstance().logout(this, listener);
        startActivity(new Intent(ChatListController.this, LoginController.class));
    }

    public void openChat(Chat c){
        Intent i = new Intent(ChatListController.this, ChatRoomController.class);
        i.putExtra("Chat", c);

        updateThread = false;
        startActivity(i);
    }
}
