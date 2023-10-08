package com.saidev.MediVeda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login_screen extends AppCompatActivity {

    Button loginButton;
    EditText userEmail, userPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);


        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.login_button);
        Button sign_up = findViewById(R.id.signUp_button);

        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login_screen.this, signUp_screen.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    loginUser();
            }
        });

    }

    private void loginUser(){
        if (userEmail.getText().toString().isEmpty()){
            userEmail.setError("Required");
        }
        else if (userPassword.getText().toString().isEmpty()){
            userPassword.setError("Required");
        }
        else {
            mAuth.signInWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(login_screen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), home_Screen.class));
                                finish();
                            }    else {
                                Toast.makeText(login_screen.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }
}