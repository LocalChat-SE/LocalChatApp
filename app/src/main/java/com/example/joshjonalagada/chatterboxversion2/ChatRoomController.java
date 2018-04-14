package com.example.joshjonalagada.chatterboxversion2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ChatRoomController extends AppCompatActivity {
    volatile boolean updateThread = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_gui);

        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                    Log.d("ChatRoomController", response.toString());
                    if ((Boolean) response.get("status")) {
                        Chat.getCurrentChat().update((JSONObject) response.get("data"));
                    } else {
                        Log.d("ChatRoomController", response.get("description").toString());
                    }
                } catch (ParseException exc) {
                    Log.e("ChatRoomController", "Could not parse: " + responseData);
                }
            }
        };

        final Thread updateLoop = new Thread(new Runnable() {
            public void run() {
                while (updateThread) {
                    try {
                        APIManager.getInstance().getChat(ChatRoomController.this, listener, Chat.getCurrentChat().getChatID());
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Log.d("ChatRoomController", "Interrupted update loop");
                        return;
                    }
                }
                updateThread = true;
            }
        });

        updateLoop.start();
    }
    public void openChatList(){
        startActivity(new Intent(ChatRoomController.this, ChatListController.class));
    }
}
