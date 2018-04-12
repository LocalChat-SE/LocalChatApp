package com.example.joshjonalagada.chatterboxversion2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.simple.JSONObject;

public class ChatListController extends AppCompatActivity {

    volatile boolean updateThread = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_gui);
        Button logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        class ChatListListener implements ResponseListener {
            public void getResult(final JSONObject response) {

                Log.d("ChatListController", response.toString());
                if ((Boolean) response.get("status")) return;

                Log.d("ChatListController", "unsuccessful");
            }
        }

        final Thread updateLoop = new Thread(new Runnable() {
            public void run() {
                while (updateThread) {
                    try {
                        Thread.sleep(5000);
                        // TODO location is hardcoded
                        APIManager.getInstance().getChats(new ChatListListener(), 32.987, -96.747);

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
        class LogoutListener implements ResponseListener {
            public void getResult(final JSONObject response) {
                if ((Boolean) response.get("status")) return;
                Log.d("ChatListController", "Error logging out. Continuing regardless.");
            }
        }
        APIManager.getInstance().logout(new LogoutListener());

        updateThread = false;
        startActivity(new Intent(ChatListController.this, LoginController.class));
    }

    public void openChat(Chat c){
        Intent i = new Intent(ChatListController.this, ChatRoomController.class);
        i.putExtra("Chat", c);

        updateThread = false;
        startActivity(i);
    }
}
