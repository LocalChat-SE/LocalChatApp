package com.example.joshjonalagada.chatterboxversion2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatRoomController extends AppCompatActivity {

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

    volatile boolean doUpdate = true;
    Thread updateThread;

    ListView messageListView;
    Date lastCheck;
    EditText messageField;
    Button sendButton;
    Button metaButton;

    MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_gui);

        // move the activity up when the keyboard is opened
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ArrayList<Message> history = Chat.getCurrentChat().getHistory();
        adapter = new MessageAdapter(this, android.R.layout.simple_list_item_1, history);

        messageListView = findViewById(R.id.messageList);
        messageListView.setAdapter(adapter);

        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = ((Message) parent.getItemAtPosition(position)).getUser();

                Chat currentChat = Chat.getCurrentChat();
                Enrolled enrollment = currentChat.getEnrollment(user);

                doUpdate = false;

                Intent i = new Intent(ChatRoomController.this, ModerateUserController.class);
                i.putExtra("Enrollment", enrollment);
                i.putExtra("User", user);
                i.putExtra("Chat", currentChat);
                startActivity(i);
            }
        });

        messageField = findViewById(R.id.messageField);

        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButton.setEnabled(false);

                sendMessage();
            }
        });

        metaButton = findViewById(R.id.metaButton);
        metaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdate = false;
                startActivity(new Intent(ChatRoomController.this, ChatInfoController.class));
            }
        });


        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                    Log.d("ChatRoomController", response.toString());

                    if (String.valueOf(response.get("description")).equals("user is banned")) {
                        Chat.setCurrentChat(null);
                        startActivity(new Intent(ChatRoomController.this, ChatListController.class));
                        return;
                    }

                    if ((Boolean) response.get("status")) {
                        sendButton.setEnabled(true);

                        // update the timestamp on success
                        lastCheck = new Date();
                        Chat.getCurrentChat().update((JSONObject) response.get("data"));

                        // if no new messages, then return early
                        if (((JSONArray) ((JSONObject) response.get("data")).get("messages")).size() == 0) return;

                        // update adapter and autoscroll to the last message
                        adapter.notifyDataSetChanged();
                        messageListView.post(new Runnable() {
                            @Override
                            public void run() {
                                messageListView.setSelection(adapter.getCount() - 1);
                            }
                        });

                    } else {
                        Log.d("ChatRoomController", response.get("description").toString());
                    }
                } catch (ParseException exc) {
                    Log.e("ChatRoomController", "Could not parse: " + responseData);
                }
            }
        };

        updateThread = new Thread(new Runnable() {
            public void run() {
                while (doUpdate) {
                    try {
                        String dateString;
                        if (lastCheck == null) dateString = "";
                        else
                            dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(lastCheck);
                        APIManager.getInstance().getChat(ChatRoomController.this, listener, Chat.getCurrentChat().getChatID(), dateString);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d("ChatRoomController", "Interrupted update loop");
                        return;
                    }
                }
                doUpdate = true;
            }
        });
    }

    private void sendMessage() {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                    messageField.setText("");
                    // output debug info from server
                    Log.d("ChatRoomController", String.valueOf(response.get("description")));

                } catch (ParseException exc) {
                    Log.e("ChatRoomController", "Could not parse: " + responseData);
                }
            }
        };

        String chatID = Chat.getCurrentChat().getChatID();
        String message = messageField.getText().toString();
        APIManager.getInstance().sendMessage(this, listener, chatID, message);
    }

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
}
