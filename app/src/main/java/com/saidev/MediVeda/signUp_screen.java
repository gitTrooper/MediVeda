package com.saidev.MediVeda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class signUp_screen extends AppCompatActivity {

    EditText userName, userAge, userPhone, userEmail, userPassword;
    Button signUpButton, loginButton;
    private FirebaseAuth mAuth;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);


        mAuth = FirebaseAuth.getInstance();

        CardView card_one = findViewById(R.id.name_card);
        CardView card_two = findViewById(R.id.age_card);
        CardView card_three = findViewById(R.id.phone_card);
        CardView card_four = findViewById(R.id.email_card);
        CardView card_five = findViewById(R.id.password_card);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        card_one.setAnimation(fadeInAnimation);
        card_two.setAnimation(fadeInAnimation);
        card_three.setAnimation(fadeInAnimation);
        card_four.setAnimation(fadeInAnimation);
        card_five.setAnimation(fadeInAnimation);

        // Creating a custom dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.email_spinner);
        dialog.setCancelable(false);
        loginButton = dialog.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), login_screen.class); // Replace with your main activity class
                    startActivity(intent);
                    finish(); // Close the current activity
            }
        });

        userName = findViewById(R.id.user_name);
        userAge = findViewById(R.id.user_age);
        userPhone = findViewById(R.id.user_phone);
        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);

        signUpButton = findViewById(R.id.sign_up_btn);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference reference = database.collection("Users").document(userEmail.getText().toString());
                Map<String, Object> userData = new HashMap<>();
                userData.put("UserName", userName.getText().toString());
                userData.put("Phone", userPhone.getText().toString());
                userData.put("Age", userAge.getText().toString());
                reference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //DATA PUSHED TO FIRESTORE
                    }
                });
            }
        });


    }

    private void createUser(){
        if (userName.getText().toString().trim().isEmpty()){
            userName.setError("Required");
        }
        else if (userAge.getText().toString().trim().isEmpty()){
            userAge.setError("Required");
        }
        else if (userEmail.getText().toString().trim().isEmpty()){
            userEmail.setError("Required");
        }
        else if (userPassword.getText().toString().trim().isEmpty()){
            userPassword.setError("Required");
        }
        else if (userPhone.getText().toString().trim().isEmpty()){
            userPhone.setError("Required");
        }
        else {
            Handler handler = new Handler();
            int delayMilliseconds = 1500; // 2 seconds

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This code will run after the specified delay (2 seconds)
                    // Put your task here
                    mAuth.createUserWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        assert user != null;
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(signUp_screen.this, "Email Verification Link Sent Successfully", Toast.LENGTH_SHORT).show();
                                                    dialog.show();
                                                }
                                                else {
                                                    Toast.makeText(signUp_screen.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                                    }
                                    else {
                                        Toast.makeText(signUp_screen.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }, delayMilliseconds);

        }
    }


}