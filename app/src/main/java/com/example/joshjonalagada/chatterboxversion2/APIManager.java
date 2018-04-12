package com.example.joshjonalagada.chatterboxversion2;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// this is implemented where the call an APIManager method is made. getResult is passed the json response.
interface ResponseListener {
    void getResult(JSONObject response);
}

public class APIManager {

    private static final String api_key = "SecretKey";

    private static final String TAG = "APIManager";
    private static APIManager instance = null;

    private static final String prefixURL = "http://shoemate.net:8888/";

    public static synchronized APIManager getInstance() {
        if (null == instance)
            instance = new APIManager();
        return instance;
    }

    private void sendPOST(String endpoint, RequestBody body, final ResponseListener listener) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://shoemate.net:8888/" + endpoint)
                .header("Accept-Encoding", "identity")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                try {
                    listener.getResult((JSONObject) new JSONParser().parse(responseData));
                } catch (ParseException e) {
                    Log.e(TAG, "Could not parse: " + responseData);
                }
            }

            @Override
            public void onFailure(Call call, final IOException exc) {
                // TODO java.net.ProtocolException: unexpected end of stream
                // This error occurs randomly, still need to look into. Doesn't seem to be happening now?
                Log.e(TAG, String.valueOf(exc));
            }
        });
    }

    public void getUser(ResponseListener listener, String userID, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("username", userID)
                .add("password", password)
                .add("api_key", api_key)
                .build();

        this.sendPOST("login", formBody, listener);
    }

    // right now this just deletes the cookie
    public void logout(ResponseListener listener) {
        RequestBody formBody = new FormBody.Builder()
                .add("api_key", api_key)
                .build();
        this.sendPOST("logout", formBody, listener);
    }

    public void setUser(ResponseListener listener, String userID, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("username", userID)
                .add("password", password)
                .add("api_key", api_key)
                .build();

        this.sendPOST("new_user", formBody, listener);
    }

    public void getChats(ResponseListener listener, float lat, float lon) {
        RequestBody formBody = new FormBody.Builder()
                .add("location", "Point(" +
                        String.valueOf(lat) + " " +
                        String.valueOf(lon) + ")")
                .add("api_key", api_key)
                .build();

        this.sendPOST("get_nearby_chats", formBody, listener);
    }

    public void getChat(ResponseListener listener, String chatID) {
        RequestBody formBody = new FormBody.Builder()
                .add("chatID", chatID)
                .add("limit", "100")
                .add("offset", "0")
                .add("api_key", api_key)
                .build();

        this.sendPOST("get_chat", formBody, listener);
    }

    public void setChat(ResponseListener listener, String chatID) {
        //TODO not implemented
    }

    public void sendMessage(ResponseListener listener, String chatID, String message) {
        RequestBody formBody = new FormBody.Builder()
                .add("chat_id", chatID)
                .add("value", message)
                .add("api_key", api_key)
                .build();

        this.sendPOST("new_message", formBody, listener);
    }

    public void setEnrollment(ResponseListener listener, String chatID, String userID, String action) {
        RequestBody formBody;
        if (action.equals("moderator")) {
            formBody = new FormBody.Builder()
                    .add("chat_id", chatID)
                    .add("user_id", userID)
                    .add("api_key", api_key)
                    .build();
        }
        else if (action.equals("ban")) {
            formBody = new FormBody.Builder()
                    .add("chat_id", chatID)
                    .add("user_id", userID)
                    .add("api_key", api_key)
                    .build();
        }
        else if (action.equals("unban")) {
            formBody = new FormBody.Builder()
                    .add("chat_id", chatID)
                    .add("user_id", userID)
                    .add("api_key", api_key)
                    .build();
        } else return;

        this.sendPOST("set_enrollment", formBody, listener);
    }
}
