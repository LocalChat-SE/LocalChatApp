package com.example.joshjonalagada.chatterboxversion2;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class ChatListController extends AppCompatActivity {

    // adapter for displaying chats in a ListView within the chat list menu
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

    // turn this off to disable polling for groups
    volatile boolean doUpdate = true;
    Thread updateThread;
    FusedLocationProviderClient mFusedLocationClient;
    long lat, lon;

    private ArrayList<Chat> allChats = new ArrayList<>();
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list_gui);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lat = (long) location.getLatitude();
                lon = (long) location.getLongitude();
            }
        });

        Button createGroupButton = findViewById(R.id.createButton);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat();
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
                openChat((Chat) parent.getItemAtPosition(position));
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

        updateThread = new Thread(new Runnable() {
            public void run() {
                while (doUpdate) {
                    try {
                        // TODO location is hardcoded
                        APIManager.getInstance().getChats(ChatListController.this, listener, lat, lon);
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        Log.d("ChatListController", "Interrupted update loop");
                        return;
                    }
                }
                doUpdate = true;
            }
        });
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

        doUpdate = false;
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

    // run the update thread when the chat list controller is active
    @Override
    protected void onPause() {
        doUpdate = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateThread.start();
    }

    private void createChat() {
        doUpdate = false;
        startActivity(new Intent(ChatListController.this, CreateRoomController.class));
    }

    private void openChat(Chat chat) {
        Chat.setCurrentChat(chat);
        doUpdate = false;
        startActivity(new Intent(ChatListController.this, ChatRoomController.class));
    }
}