package com.example.joshjonalagada.chatterboxversion2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class ChatListController extends AppCompatActivity {

    // turn this off to disable polling for groups
    volatile boolean updateThread = true;

    private ArrayList<Chat> allChats = new ArrayList<>();
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_gui);

        Button createGroupButton = findViewById(R.id.createButton);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateThread = false;
                startActivity(new Intent(ChatListController.this, CreateRoomController.class));
            }
        });

        Button logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        adapter = new ChatAdapter(this, android.R.layout.simple_list_item_1, allChats);
        ListView chatListView = findViewById(R.id.chatList);
        chatListView.setAdapter(adapter);

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chat.setCurrentChat((Chat) parent.getItemAtPosition(position));
                updateThread = false;
                startActivity(new Intent(ChatListController.this, ChatRoomController.class));
            }
        });

        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                    if ((Boolean) response.get("status")) {
                        updateChats(response);
                    } else {
                        Log.d("ChatListController", response.get("description").toString());
                    }
                } catch (JSONException exc) {
                    Log.e("ChatListController", "Invalid json structure: " + responseData);
                } catch (ParseException exc) {
                    Log.e("ChatListController", "Could not parse: " + responseData);
                }
            }
        };

        final Response.ErrorListener listenerError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ChatListController", "Chat update failed.");
            }
        };

        final Thread updateLoop = new Thread(new Runnable() {
            public void run() {
                while (updateThread) {
                    try {
                        // TODO location is hardcoded
                        APIManager.getInstance().getChats(ChatListController.this, listener, 32.987, -96.747);
                        Thread.sleep(1000);

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

    private void updateChats(JSONObject json) throws JSONException {
        JSONArray chats = (JSONArray) json.get("data");

        allChats.clear();
        // reconstruct chat list
        for (int i = 0; i < chats.size(); i++) {
            allChats.add(new Chat((JSONObject) chats.get(i)));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        updateThread = false;
        super.onPause();
    }
}


class ChatAdapter extends ArrayAdapter<Chat> {
    public ChatAdapter(Context context, int textViewResourceId, ArrayList<Chat> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final Chat chat = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_chat, parent, false);
        }

        // Lookup view for data population
        TextView roomName = convertView.findViewById(R.id.roomName);
        TextView roomDescription = convertView.findViewById(R.id.roomDescription);
        TextView roomDistance = convertView.findViewById(R.id.roomDistance);

        // Populate the data into the template view using the data object
        roomName.setText(chat.getName());
        roomDescription.setText(chat.getDescription());
        roomDistance.setText(String.valueOf(chat.getDistance()));

        // Return the completed view to render on screen
        return convertView;
    }
}