package com.example.joshjonalagada.chatterboxversion2;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;

public class APIManager {

    private static final String api_key = "SecretKey";

    private static APIManager instance = null;

    private static final String prefixURL = "http://shoemate.net:8888/";

    public static synchronized APIManager getInstance() {
        if (null == instance) instance = new APIManager();
        return instance;
    }

    private void sendPOST(Context context, final String endpoint, JSONObject body, final Response.Listener<String> listener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String URL = prefixURL + endpoint;
        final String requestBody = body.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("APIManager", error.toString());
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                Log.d("APIMANAGER", response.toString());
//                String responseString = "";
//                if (response != null) {
//                    responseString = String.valueOf(response.statusCode);
//                    // can get more details such as response.headers
//                }
//                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//            }
        };

        requestQueue.add(stringRequest);
    }

    public void getUser(Context context, Response.Listener<String> listener, String userID, String password) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", userID);
        jsonBody.put("password", password);
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, "login", jsonBody, listener);
    }

    // right now this just deletes the cookie
    public void logout(Context context, Response.Listener<String> listener) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, "logout", jsonBody, listener);
    }

    public void setUser(Context context, Response.Listener<String> listener, String userID, String password) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", userID);
        jsonBody.put("password", password);
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, "new_user", jsonBody, listener);
    }

    public void getChats(Context context, Response.Listener<String> listener, double lat, double lon) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("location", "Point(" +
                String.valueOf(lat) + " " +
                String.valueOf(lon) + ")");
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, "get_nearby_chats", jsonBody, listener);
    }

    public void getChat(Context context, Response.Listener<String> listener, String chatID) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("chat_id", chatID);
        jsonBody.put("limit", "100");
        jsonBody.put("offset", "0");
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, "get_chat", jsonBody, listener);
    }

    public void setChat(Context context, Response.Listener<String> listener, String chatID) {
        //TODO not implemented
    }

    public void sendMessage(Context context, Response.Listener<String> listener, String chatID, String message) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("chat_id", chatID);
        jsonBody.put("value", message);
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, "new_message", jsonBody, listener);
    }

    public void setEnrollment(Context context, Response.Listener<String> listener, String chatID, String userID, String action) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("api_key", api_key);
        jsonBody.put("chat_id", chatID);
        jsonBody.put("user_id", userID);

        // TODO
        if (action.equals("moderator")) {
        } else if (action.equals("ban")) {
        } else if (action.equals("unban")) {
        } else return;

        this.sendPOST(context, "set_enrollment", jsonBody, listener);
    }
}
