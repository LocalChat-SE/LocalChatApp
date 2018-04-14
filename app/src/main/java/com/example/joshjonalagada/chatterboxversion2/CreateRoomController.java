package com.example.joshjonalagada.chatterboxversion2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CreateRoomController extends AppCompatActivity {

    EditText groupNameField;
    EditText groupDescriptionField;

    TextView locationTextView;
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        groupDescriptionField = findViewById(R.id.groupDescriptionField);
        groupNameField = findViewById(R.id.groupNameField);

        locationTextView = findViewById(R.id.locationTextView);
        errorTextView = findViewById(R.id.errorTextView);

        // TODO: set the locationTextView text based on current location

        Button createGroupButton = findViewById(R.id.createGroupButton);
        createGroupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createRoom();
            }
        });
    }

    public void createRoom() {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    final JSONObject response = (JSONObject) new JSONParser().parse(responseData);

                    // group was created, so switch to the chat room tab
                    if ((Boolean) response.get("status")) {

                        Chat.setCurrentChat(new Chat((JSONObject) response.get("data")));
                        startActivity(new Intent(CreateRoomController.this, ChatRoomController.class));
                    } else {
                        // display error prompt returned from server
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorTextView.setText((String) response.get("description"));
                            }
                        });
                    }
                } catch (ParseException exc) {
                    Log.e("ChatListController", "Could not parse: " + responseData);
                }
            }
        };

        // gather information necessary for new group and submit to APIManager
        String name = groupNameField.getText().toString();
        String description = groupDescriptionField.getText().toString();

        // TODO locations should not be hardcoded
        long lat = 22;
        long lon = 22;

        APIManager.getInstance().newChat(this, listener, name, description, lat, lon);
    }
}
