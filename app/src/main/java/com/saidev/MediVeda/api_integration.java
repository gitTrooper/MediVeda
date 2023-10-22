package com.saidev.MediVeda;

import android.content.Context;
import android.widget.Toast;


import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class api_integration {

    private static final String CHATGPT_ENDPOINT = "https://api.openai.com/v1/chat/completions";


    public interface ChatGPTResponseListener {
        void onResponse(String response);
        void onError(VolleyError error);
    }



    public static void generateResponse(Context ctx, String input, final ChatGPTResponseListener listener, List<String> conversationHistory) {



        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("model", "gpt-3.5-turbo");

            JSONArray messageArr = new JSONArray();

            // Add previous messages to the conversation history
            for (String message : conversationHistory) {
                JSONObject prevMessage = new JSONObject();
                prevMessage.put("role", "system");
                prevMessage.put("content", message);
                messageArr.put(prevMessage);
            }

            // Add the user's new message
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", input);
            messageArr.put(userMessage);

            jsonRequest.put("messages", messageArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, CHATGPT_ENDPOINT, jsonRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract the bot's response
                            String botResponse = response.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                                    .getString("content");

                            // Add the user's new message to the conversation history
                            conversationHistory.add(input);
                            conversationHistory.add(botResponse);
                            // Pass the bot's response back to the listener
                            listener.onResponse(botResponse);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                        Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer ");

                return headers;
            }
        };

        int intTimeout = 30000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        Volley.newRequestQueue(ctx).add(jsonObjectRequest);
    }



}