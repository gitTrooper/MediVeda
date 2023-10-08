package com.saidev.MediVeda.features;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saidev.MediVeda.R;
import com.saidev.MediVeda.api_integration;
import com.saidev.MediVeda.message;
import com.saidev.MediVeda.messageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class chatBot_ui extends AppCompatActivity {

    RecyclerView chatHolderRecycler;
    FloatingActionButton send_text_btn;
    EditText msg_editor_box;
    Dialog dialog;
    List<message> messageList = new ArrayList<>();
    messageAdapter adapter;
    List<String> conversationHistory = new ArrayList<>();

    int maxInteractionsPerDay = 3; // Set the maximum interactions allowed per day
    FirebaseAuth mAuth;
    String userEmail;
    long lastInteractionTimestamp;
    int interactionCount;
    TextView counter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CardView inputCard;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot_ui);

        chatHolderRecycler = findViewById(R.id.msgRecyclerView);
        send_text_btn = findViewById(R.id.msgSendBtn);
        msg_editor_box = findViewById(R.id.msgEditBox);
        counter = findViewById(R.id.interactionCounter);
        inputCard = findViewById(R.id.cardView);

        mAuth = FirebaseAuth.getInstance();
        userEmail = mAuth.getCurrentUser().getEmail();

        Intent intent = getIntent();
        String PRE_DEFINED_PROMPT = intent.getStringExtra("PROMPT");

        // Creating a custom dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.bot_typing_dialog);
        dialog.setCancelable(false);
        dialog.show();
        adapter = new messageAdapter(messageList);
        chatHolderRecycler.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatHolderRecycler.setLayoutManager(linearLayoutManager);

        SharedPreferences sharedPreferences = getSharedPreferences("ChatBotPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        lastInteractionTimestamp = sharedPreferences.getLong("lastInteractionTimestamp", 0);
        interactionCount = sharedPreferences.getInt("interactionCount", 0);
        counter.setText("Limit : "+String.valueOf(maxInteractionsPerDay-interactionCount));


        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp - lastInteractionTimestamp >= 24 * 60 * 60 * 1000) {
            // 24 hours have passed, reset the interaction count
            interactionCount = 0;
        }

        if (interactionCount >= maxInteractionsPerDay) {
            // User has exceeded the interaction limit
            // Handle this case, for example, by displaying a message to the user
            counter.setText("Limit : "+String.valueOf(maxInteractionsPerDay-interactionCount));
            inputCard.setVisibility(View.GONE);
            send_text_btn.setVisibility(View.GONE);
            addToChat("You Have Exhausted Your Daily Limit. Please Check Back After 24 Hours", message.SENT_BY_BOT);
            dialog.dismiss();
        } else {
            // Allow the user to interact with the chatbot

            api_integration.generateResponse(getApplicationContext(), PRE_DEFINED_PROMPT, new api_integration.ChatGPTResponseListener() {
                @Override
                public void onResponse(String response) {
                    addToChat(response, message.SENT_BY_BOT);
                    dialog.dismiss();
                }

                @Override
                public void onError(VolleyError error) {

                }
            },conversationHistory);

        }


        send_text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String user_response = msg_editor_box.getText().toString();
                addToChat(user_response, message.SENT_BY_USER);
                msg_editor_box.setText("");
                api_integration.generateResponse(getApplicationContext(), user_response, new api_integration.ChatGPTResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toLowerCase().contains("----------thank you----------"))
                        {
                            // Increment the interaction count
                            interactionCount++;
                            // Update the last interaction timestamp
                            editor.putLong("lastInteractionTimestamp", currentTimestamp);
                            editor.putInt("interactionCount", interactionCount);
                            editor.apply();
                            counter.setText("Limit : "+String.valueOf(maxInteractionsPerDay-interactionCount));
                            // Example data to be added
                            Map<String, Object> report = new HashMap<>();
                            report.put("Report", response);
                            db.collection("Users").document(mAuth.getCurrentUser().getEmail())
                                            .update("Prakruti Report",report)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    });

                            addToChat(response, message.SENT_BY_BOT);
                        }
                        else {
                            addToChat(response, message.SENT_BY_BOT);
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                },conversationHistory);
            }
        });

    }

     void addToChat(String message, String sentBy){
        runOnUiThread(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                messageList.add(new message(message, sentBy));
                adapter.notifyDataSetChanged();
                chatHolderRecycler.smoothScrollToPosition(adapter.getItemCount());
            }
        });
    }



}