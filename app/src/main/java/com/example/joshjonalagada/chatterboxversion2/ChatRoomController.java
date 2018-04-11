package com.example.joshjonalagada.chatterboxversion2;

/**
 * Created by Tayfun Nalbantoglu on 4/10/2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ChatRoomController extends AppCompatActivity {
    Chat currentChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_gui);
        currentChat = (Chat) getIntent().getSerializableExtra("Chat");
    }
    public void openChatList(){
        startActivity(new Intent(ChatRoomController.this, ChatListController.class));
    }
}
