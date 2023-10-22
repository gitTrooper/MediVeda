package com.saidev.MediVeda.menuItemFragments;

import static android.content.Context.MODE_PRIVATE;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.saidev.MediVeda.R;
import com.saidev.MediVeda.features.chatBot_ui;
import com.saidev.MediVeda.features.emergencyServices;
import com.saidev.MediVeda.features.hospitalLocator;
import com.saidev.MediVeda.login_screen;

import java.util.ArrayList;
import java.util.List;

public class homeScreen extends Fragment {

    ImageSlider homeSlider;
    CardView prakrutiAssessment, disease_diagnosis;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    CardView HospitalLocator, emergencyService;
    String PRE_DEFINED_PROMPT_PRAKRUTI = "Hey, I want you to be an ayurvedic doctor and ask me questions one by one to determine my prakriti type. Start the task immediately by greeting the user in a single line. Make sure that there should be three options in an order of a, b, c in each of your question, so the user have three options for each question and he answers them by select any option from a, b or c. Ask the next question only when the user has answered the previous question . After you determined the prakriti type of user, give him suggestion to improve his lifestyle and diet plan. ***NOTE: 1. All points are necessary. 2. In the first question, you should ask about the age of the user. 3. IN THE FIRST QUESTION, THE USER WILL HAVE TO ENTER HIS AGE MANUALLY, YOU WILL NOT PROVIDE HIM WITH OPTIONS(ONLY FOR THE FIRST QUESTION). ONCE THE USER WILL PROVIDE HIS AGE THEN ONLY YOU WILL MOVE TO THE NEXT QUESTION. 4. In the second question, you should ask the user about his gender. 5. You need to ask a total of at least 15-17 questions to improve the accuracy of the result. 6. Ask every question one by one, i.e., first you will have to ask a question and then provide the user with options to choose from, once the user opts for a answer then you will ask the next question. 7. At the very final end of the prakruti assesment, after suggesting the user for his lifestyle and diet plans, show him this message \"----------THANK YOU----------\"" + "*";
    String PRE_DEFINED_PROMPT_DISEASE_DIAGNOSIS = "You need to behave like a professional doctor, communicate in English language, introduce yourself as a Chat bot named \"Med-O\". Ask questions to the user about the symptoms he is facing and also provide him a list to choose from, and based on his response ask further questions one by one and provide options to the user to choose from, and try to diagnose the disease he is having as accurately as possible. NOTE: 1. Every question should be Multiple Choice Question type that is it must have option(s) to choose from. 2. Reflect the Diagnosed problem(in the last) in bold letters. 3. Always ask the question ONE BY ONE: Ask a question to the user and provide him with options to choose from, once the user answers the first question then only ask the second question. 3. Give a list of disease that probably happen to the user after the questionaire round. 4. Always add a specific set of questions. 5. Add an option of specify your response if the user doe not find any of the options relevant. 6. If the user chooses to specify his response, ask him to specify it in the next question.";
    NativeAd mNativeAd;

    Dialog dialog; VideoView video; Button startAssessment; ImageView play;
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
        emergencyService = v.findViewById(R.id.emergencyServiceCard);
        MobileAds.initialize(v.getContext(), initializationStatus -> {});
        // Get the NativeAdView.
        loadNativeAd();

        dialog = new Dialog(v.getContext());
        dialog.setContentView(R.layout.introduction_dialoge);
        video = dialog.findViewById(R.id.videoView);
        play = dialog.findViewById(R.id.video_play);
        startAssessment = dialog.findViewById(R.id.prakruti_assessment);

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

                    homeSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemSelected(int i) {
                            String youtubeVideoUrl = dataSnapshot.child("video_url").getValue().toString();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeVideoUrl));
                            intent.putExtra("force_fullscreen", true);  // Optional: Force fullscreen in the YouTube app
                            startActivity(intent);
                        }

                        @Override
                        public void doubleClick(int i) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(v.getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });



        prakrutiAssessment.setOnClickListener(view -> dialog.show());

        startAssessment.setOnClickListener(view -> {
            Intent intent = new Intent(v.getContext(), chatBot_ui.class);
            intent.putExtra("PROMPT",PRE_DEFINED_PROMPT_PRAKRUTI);
            dialog.dismiss();
            startActivity(intent);
        });


        play.setOnClickListener(view -> {
            play.setVisibility(View.GONE);
            video.start();
        });

       disease_diagnosis.setOnClickListener(view -> {
           Intent intent = new Intent(v.getContext(), chatBot_ui.class);
           intent.putExtra("PROMPT",PRE_DEFINED_PROMPT_DISEASE_DIAGNOSIS);
           startActivity(intent);
       });


        HospitalLocator.setOnClickListener(view -> startActivity(new Intent(v.getContext(), hospitalLocator.class)));

        emergencyService.setOnClickListener(view -> startActivity(new Intent(v.getContext(), emergencyServices.class)));


        return v;
    }

    private void loadNativeAd(){

        AdLoader.Builder adLoader = new AdLoader.Builder(getContext(), "ca-app-pub-3940256099942544/2247696110");
        adLoader.forNativeAd(nativeAd -> {

            if (getActivity().isDestroyed() || getActivity().isFinishing() || getActivity().isChangingConfigurations()){
                mNativeAd.destroy();
                return;
            }

            if (mNativeAd!=null){
                mNativeAd.destroy();
            }
            mNativeAd = nativeAd;
            FrameLayout adFrame = getActivity().findViewById(R.id.NativeFrame);
            NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
            populateNativeAdView(nativeAd, adView);
            adFrame.removeAllViews();
            adFrame.addView(adView);

        });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        AdLoader NativeadLoader = adLoader.withAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Toast.makeText(getContext(), loadAdError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
            }
        }).build();

        NativeadLoader.loadAd(new AdRequest.Builder().build());

    }
    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

}