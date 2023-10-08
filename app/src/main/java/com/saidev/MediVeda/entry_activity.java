package com.saidev.MediVeda;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.MobileAds;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class entry_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Handler handler = new Handler();
        int delayMilliseconds = 2000; // 2 seconds

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // Check if user is signed in (non-null) and update UI accordingly.

                if(currentUser != null){
                    startActivity(new Intent(getApplicationContext(), home_Screen.class));
                    finish();
                }
                else {
                    startActivity(new Intent(getApplicationContext(), login_screen.class));
                    finish();
                }
            }
        },delayMilliseconds);



    }


}