package com.example.joshjonalagada.chatterboxversion2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatRoomController extends AppCompatActivity {
    volatile boolean updateThread = true;

    Date lastCheck;

    MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_gui);

        ArrayList<Message> history = Chat.getCurrentChat().getHistory();
        adapter = new MessageAdapter(this, android.R.layout.simple_list_item_1, history);

        ListView chatListView = findViewById(R.id.messageList);
        chatListView.setAdapter(adapter);

        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                    Log.d("ChatRoomController", response.toString());
                    if ((Boolean) response.get("status")) {
                        // update the timestamp on success
                        lastCheck = new Date();
                        Chat.getCurrentChat().update((JSONObject) response.get("data"));
                        adapter.notifyDataSetChanged();

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
                        String dateString;
                        if (lastCheck == null) dateString = "";
                        else dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(lastCheck);

                        APIManager.getInstance().getChat(ChatRoomController.this, listener, Chat.getCurrentChat().getChatID(), dateString);
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
}


class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(Context context, int textViewResourceId, ArrayList<Message> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final Message message = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_message, parent, false);
        }

        // Lookup view for data population
        TextView messageAuthor = convertView.findViewById(R.id.authorText);
        TextView messageText = convertView.findViewById(R.id.messageText);

        // Populate the data into the template view using the data object
        messageAuthor.setText(message.getUser().getUsername());
        messageText.setText(message.getValue());

        // Return the completed view to render on screen
        return convertView;
    }
}