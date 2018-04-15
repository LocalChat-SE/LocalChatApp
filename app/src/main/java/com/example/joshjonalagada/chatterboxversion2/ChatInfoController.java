package com.example.joshjonalagada.chatterboxversion2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class ChatInfoController extends AppCompatActivity {
    class EnrollsAdapter extends ArrayAdapter<Enrolled> {
        public EnrollsAdapter (Context context, int textViewResourceId, ArrayList<Enrolled> items) {
            super(context, textViewResourceId, items);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final Enrolled enrollment = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_enrollment, parent, false);
            }

            // Lookup view for data population
            TextView usernameText = convertView.findViewById(R.id.usernameText);
            TextView statusText = convertView.findViewById(R.id.statusText);

            // Populate the data into the template view using the data object
            usernameText.setText(enrollment.getUser().getUsername());

            if (enrollment.getIsModerator()) {
                statusText.setText("moderator");
            }
            else if (enrollment.getIsBanned()) {
                statusText.setText("banned");
            } else statusText.setText("");

            // Return the completed view to render on screen
            return convertView;
        }
    }

    Button deleteButton;
    TextView errorDeleteButton;
    ListView chatMembers;

    EnrollsAdapter adapter;

    ArrayList<Enrolled> chatEnrollments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        deleteButton = findViewById(R.id.deleteChat);
        errorDeleteButton = findViewById(R.id.errorMetaTextView);
        chatMembers = findViewById(R.id.chatMembers);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChat();
            }
        });

        chatEnrollments = Chat.getCurrentChat().getEnrollments();

        adapter = new EnrollsAdapter(this, android.R.layout.simple_list_item_1, chatEnrollments);
        chatMembers.setAdapter(adapter);
    }

    private void deleteChat() {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorDeleteButton.setText((String) response.get("Description"));
                        }
                    });
                    if ((Boolean) response.get("status")) {
                        startActivity(new Intent(ChatInfoController.this, ChatListController.class));
                    }
                } catch (ParseException exc) {
                    Log.e("ChatInfoController", "Could not parse: " + responseData);
                }
            }
        };

        APIManager.getInstance().deleteChat(this, listener, Chat.getCurrentChat().getChatID());
    }

    // redraw user list when entering each room, and update the deletion button
    @Override
    protected void onResume() {
        super.onResume();

        chatEnrollments = Chat.getCurrentChat().getEnrollments();
        adapter.notifyDataSetChanged();

        Enrolled loggedEnrollment = Chat.getCurrentChat().getEnrollment(User.getLoggedUser());

        if (loggedEnrollment != null && loggedEnrollment.getIsModerator()) {
            deleteButton.setEnabled(true);
            errorDeleteButton.setText("");
        } else {
            deleteButton.setEnabled(false);
            errorDeleteButton.setText("only moderators may delete the chat");
        }
    }
}
