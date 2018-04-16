package com.example.joshjonalagada.chatterboxversion2;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CreateRoomController extends AppCompatActivity{

    EditText groupNameField;
    EditText groupDescriptionField;

    TextView locationTextView;
    TextView errorTextView;
    FusedLocationProviderClient mFusedLocationClient;

    long lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        groupDescriptionField = findViewById(R.id.groupDescriptionField);
        groupNameField = findViewById(R.id.groupNameField);

        locationTextView = findViewById(R.id.locationTextView);
        errorTextView = findViewById(R.id.errorTextView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lat = (long)location.getLatitude();
                            lon = (long)location.getLongitude();
                            locationTextView.setText("Lat: " + String.valueOf(lat) + ", Lon: " + String.valueOf(lon));
                        }
                        else
                            locationTextView.setText("ERROR");
                    }
        });

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


        APIManager.getInstance().newChat(this, listener, name, description, lat, lon);
    }
}
