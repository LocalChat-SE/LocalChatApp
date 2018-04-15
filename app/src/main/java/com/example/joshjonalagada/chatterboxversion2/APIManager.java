package com.example.joshjonalagada.chatterboxversion2;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookieHandler;

public class APIManager {

    private static final String api_key = "SecretKey";
    private static APIManager instance = null;
    private static final String prefixURL = "http://shoemate.net:8888/";
    private CookieManager manager = new CookieManager();

    private APIManager() {
        CookieHandler.setDefault(manager);
    }

    public static synchronized APIManager getInstance() {
        if (null == instance) instance = new APIManager();
        return instance;
    }

    private void sendPOST(Context context, final Response.Listener<String> listener, final String endpoint, JSONObject body) {
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
        };

        requestQueue.add(stringRequest);
    }

    public void getUser(Context context, Response.Listener<String> listener, String userID, String password) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", userID);
        jsonBody.put("password", password);
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, listener, "login", jsonBody);
    }

    // right now this just deletes the cookie
    public void logout(Context context, Response.Listener<String> listener) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, listener, "logout", jsonBody);
    }

    public void setUser(Context context, Response.Listener<String> listener, String userID, String password) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", userID);
        jsonBody.put("password", password);
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, listener, "new_user", jsonBody);
    }

    public void getChats(Context context, Response.Listener<String> listener, double lat, double lon) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("location", "Point(" +
                String.valueOf(lat) + " " +
                String.valueOf(lon) + ")");
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, listener, "get_nearby_chats", jsonBody);
    }

    public void getChat(Context context, Response.Listener<String> listener, String chatID, String time) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("chat_id", chatID);
        jsonBody.put("time", time);
        jsonBody.put("limit", "100");
        jsonBody.put("offset", "0");
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, listener,"get_chat", jsonBody);
    }

    public void setChat(Context context, Response.Listener<String> listener, String chatID) {
        //TODO not implemented
    }

    public void newChat(Context context, Response.Listener<String> listener, String name, String description, long lat, long lon) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("name", name);
        jsonBody.put("description", description);
        jsonBody.put("location", "Point(" +
                String.valueOf(lat) + " " +
                String.valueOf(lon) + ")");
        jsonBody.put("api_key", api_key);
        this.sendPOST(context, listener, "new_chat", jsonBody);
    }

    public void sendMessage(Context context, Response.Listener<String> listener, String chatID, String message) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("chat_id", chatID);
        jsonBody.put("value", message);
        jsonBody.put("api_key", api_key);

        this.sendPOST(context, listener,"new_message", jsonBody);
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

        this.sendPOST(context, listener,"set_enrollment", jsonBody);
    }
}
