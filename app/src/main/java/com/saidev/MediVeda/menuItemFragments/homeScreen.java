package com.saidev.MediVeda.menuItemFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.saidev.MediVeda.R;
import com.saidev.MediVeda.features.chatBot_ui;
import com.saidev.MediVeda.features.hospitalLocator;
import com.saidev.MediVeda.login_screen;

import java.util.ArrayList;
import java.util.List;

public class homeScreen extends Fragment {

    ImageSlider homeSlider;
    CardView prakrutiAssessment, disease_diagnosis;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    CardView HospitalLocator;
    String PRE_DEFINED_PROMPT_PRAKRUTI = "Hey, I want you to be an ayurvedic doctor and ask me questions one by one to determine my prakriti type. Start the task immediately by greeting the user in a single line. Make sure that there should be three options in an order of a, b, c in each of your question, so the user have three options for each question and he answers them by select any option from a, b or c. Ask the next question only when the user has answered the previous question . After you determined the prakriti type of user, give him suggestion to improve his lifestyle and diet plan. ***NOTE: 1. All points are necessary. 2. In the first question, you should ask about the age of the user. 3. IN THE FIRST QUESTION, THE USER WILL HAVE TO ENTER HIS AGE MANUALLY, YOU WILL NOT PROVIDE HIM WITH OPTIONS(ONLY FOR THE FIRST QUESTION). ONCE THE USER WILL PROVIDE HIS AGE THEN ONLY YOU WILL MOVE TO THE NEXT QUESTION. 4. In the second question, you should ask the user about his gender. 5. You need to ask a total of at least 15-17 questions to improve the accuracy of the result. 6. Ask every question one by one, i.e., first you will have to ask a question and then provide the user with options to choose from, once the user opts for a answer then you will ask the next question. 7. At the very final end of the prakruti assesment, after suggesting the user for his lifestyle and diet plans, show him this message \"----------THANK YOU----------\"" + "*";
    String PRE_DEFINED_PROMPT_DISEASE_DIAGNOSIS = "behave like a professional doctor, communicate in English language, introduce yourself as a chatbot named \"Med-O\". Ask questions to the user about the symptoms he is facing and also provide him a list to choose from, and based on his response ask further questions one by one and provide options to the user to choose from, and try to diagnose the disease he is having as accurately as possible. * NOTE: 1. Every question should be mcq type i.e. it must have option(s) to choose from. 2. Reflect the Diagnosed problem(in the last) in bold letters. 3. Always ask the question one by one: Ask a question to the user and provide him with options to choose from, once the user answers the first question then only ask the second question. *";


    public homeScreen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home_screen, container, false);

        //ID INITIALIZATION
        disease_diagnosis = v.findViewById(R.id.diseaseDiagnosis);
        homeSlider = v.findViewById(R.id.home_banner_slider);
        prakrutiAssessment = v.findViewById(R.id.prakruti_assessment);
        HospitalLocator = v.findViewById(R.id.hospitalLocator);


        if (!mAuth.getCurrentUser().isEmailVerified()){
            Toast.makeText(v.getContext(), "Email Not Verified", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(v.getContext(), login_screen.class));
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
        else {
            // Check if the toast has already been shown
            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean emailVerifiedToastShown = sharedPreferences.getBoolean("emailVerifiedToastShown", false);

            if (!emailVerifiedToastShown) {
                // Show the toast
                Toast.makeText(v.getContext(), "Email Verified Successfully", Toast.LENGTH_SHORT).show();

                // Set the flag to indicate that the toast has been shown
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("emailVerifiedToastShown", true);
                editor.apply();
            }
        }




        final List<SlideModel> databaseImages = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Home Slider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    databaseImages.add(new SlideModel(dataSnapshot.child("img_url").getValue().toString(), ScaleTypes.FIT));
                    homeSlider.setImageList(databaseImages,ScaleTypes.FIT);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(v.getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });

        prakrutiAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(v.getContext(), chatBot_ui.class);
                intent.putExtra("PROMPT",PRE_DEFINED_PROMPT_PRAKRUTI);
                startActivity(intent);
            }
        });

        disease_diagnosis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(v.getContext(), chatBot_ui.class);
                intent.putExtra("PROMPT",PRE_DEFINED_PROMPT_DISEASE_DIAGNOSIS);
                startActivity(intent);
            }
        });


        HospitalLocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(v.getContext(), hospitalLocator.class));
            }
        });




        return v;
    }
}