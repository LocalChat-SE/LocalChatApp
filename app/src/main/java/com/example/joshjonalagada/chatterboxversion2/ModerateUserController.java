package com.example.joshjonalagada.chatterboxversion2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ModerateUserController extends AppCompatActivity {

    Button banButton;
    Button moderateButton;
    TextView errorTextView;
    TextView usernameTextView;

    User user;
    Enrolled enrollment;
    Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderate_user);

        Intent intent = getIntent();
        user = (User) intent.getExtras().get("User");
        enrollment = (Enrolled) intent.getExtras().get("Enrollment");
        chat = (Chat) intent.getExtras().get("Chat");

        usernameTextView = findViewById(R.id.userText);
        banButton = findViewById(R.id.banButton);
        moderateButton = findViewById(R.id.moderateButton);
        errorTextView = findViewById(R.id.modErrorTextView);

        updateMenu();

        banButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseData) {

                        try {
                            final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                            Log.d("ChatRoomController", response.toString());
                            if ((Boolean) response.get("status")) {
                                enrollment.setIsBanned(!enrollment.getIsBanned());
                                updateMenu();
                            } else {
                                errorTextView.setText((String) response.get("description"));
                            }
                        } catch (ParseException exc) {
                            Log.e("ModerateUserController", "Could not parse: " + responseData);
                        }
                        Log.d("ModerateUserController", responseData);
                    }
                };

                APIManager.getInstance().setBanned(ModerateUserController.this, listener, chat.getChatID(), user.getUsername(), !enrollment.getIsBanned());
            }
        });

        moderateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseData) {

                        try {
                            final JSONObject response = (JSONObject) new JSONParser().parse(responseData);
                            Log.d("ModerateUserController", response.toString());
                            if ((Boolean) response.get("status")) {
                                enrollment.setIsModerator(!enrollment.getIsModerator());
                                updateMenu();
                            } else {
                                errorTextView.setText((String) response.get("description"));
                            }
                        } catch (ParseException exc) {
                            Log.e("ModerateUserController", "Could not parse: " + responseData);
                        }
                        Log.d("ModerateUserController", responseData);
                    }
                };

                APIManager.getInstance().setModerator(ModerateUserController.this, listener, chat.getChatID(), user.getUsername());
            }
        });
    }

    private void updateMenu() {
        final boolean loggedUserIsModerator = chat.getEnrollment(User.getLoggedUser()).getIsModerator();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                usernameTextView.setText(user.getUsername());

                // enable/disable depending on if current user is a moderator
                banButton.setEnabled(loggedUserIsModerator);
                banButton.setEnabled(loggedUserIsModerator);

                if (loggedUserIsModerator) {
                    errorTextView.setText("");
                    boolean banned = enrollment.getIsBanned();
                    boolean moderator = enrollment.getIsModerator();

                    if (moderator) {
                        banButton.setEnabled(false);
                        moderateButton.setEnabled(false);

                        banButton.setText("Cannot Ban");
                        moderateButton.setText("Cannot Demote");
                        errorTextView.setText("user is a moderator");
                    } else if (banned) {
                        banButton.setEnabled(true);
                        moderateButton.setEnabled(true);

                        banButton.setText("Unban User");
                        moderateButton.setText("Unban and Promote User");
                        errorTextView.setText("");
                    } else {
                        banButton.setEnabled(true);
                        moderateButton.setEnabled(true);

                        banButton.setText("Ban User");
                        moderateButton.setText("Promote User");
                        errorTextView.setText("");
                    }
                } else {
                    banButton.setEnabled(false);
                    moderateButton.setEnabled(false);

                    banButton.setText("Ban User");
                    moderateButton.setText("Promote User");
                    errorTextView.setText("must be a moderator to ban or promote user");
                }
            }
        });
    }
}
