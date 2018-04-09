package com.example.joshjonalagada.chatterboxversion2;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

// this is implemented
interface ResponseListener {
    void getResult(JSONObject response);
}

public class APIManager {

    private static final String api_key = "SecretKey";

    private static final String TAG = "APIManager";
    private static APIManager instance = null;

    private static final String prefixURL = "http://shoemate.net:8888/";

    private RequestQueue requestQueue;

    private APIManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized APIManager getInstance(Context context) {
        if (null == instance)
            instance = new APIManager(context);
        return instance;
    }

    //this is so you don't need to pass context each time
    public static synchronized APIManager getInstance() {

        // must initialize with context on first time-- Context context = this;
        if (null == instance) {
            throw new IllegalStateException(APIManager.class.getSimpleName() +
                    " is not initialized, call getInstance(Context myContext) first");
        }
        return instance;
    }

    private void sendPOST(String endpoint, JSONObject jsonParams, final ResponseListener listener) {

        String url = prefixURL + endpoint;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "GOT RESPONSE");
                        Log.d(TAG + " : ", "sendPOST Response : " + response.toString());
                        if (null != response.toString()) listener.getResult(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (null != error.networkResponse) {
                            Log.d(TAG + " : ", "Error Response code: " + error.networkResponse.statusCode);
                        }
                    }
                });

        requestQueue.add(request);
    }


    public void loginUser(String userID, final String password) {

        class LoginListener implements ResponseListener {
            public void getResult(JSONObject response) {
                Log.d(TAG + " : ", "In login callback");
                Log.d(TAG + " : ", String.valueOf(response));
            }
        }

        Log.d(TAG, "In loginUser function");
        JSONObject POST = new JSONObject();
        try {
            POST.put("api_key", APIManager.api_key);
            POST.put("username", userID);
            POST.put("password", password);
            this.sendPOST("login", POST, new LoginListener());
        } catch (JSONException e) {
            Log.d(TAG + " : ", String.valueOf(e));
        }
    }
}