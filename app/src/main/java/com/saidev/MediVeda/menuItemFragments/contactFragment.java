package com.saidev.MediVeda.menuItemFragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.saidev.MediVeda.R;


public class contactFragment extends Fragment {

    public contactFragment() {
        // Required empty public constructor
    }

    CardView emailContact, instaContact, twitterContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        emailContact = v.findViewById(R.id.emailCard);
        instaContact = v.findViewById(R.id.instaCard);
        twitterContact = v.findViewById(R.id.twitterCard);

        emailContact.setOnClickListener(view -> {
            // Replace 'recipient@example.com' with the actual recipient email address
            String recipientEmail = "contact@mediveda.com";

            // Replace 'Email Subject' with the subject of the email
            String emailSubject = "Your Reason For Contact";

            // Replace 'Email body text here...' with the body of the email
            String emailBody = "Email body text here...";

            // Create an Intent with ACTION_SENDTO to open the Gmail app
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + recipientEmail)); // Specify recipient email address
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject); // Specify email subject
            intent.putExtra(Intent.EXTRA_TEXT, emailBody); // Specify email body text

            // Verify that there's an activity to handle the intent before starting it
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // If there is no email app installed, you can handle the situation here
                // For example, you can show a Toast message to inform the user.
                Toast.makeText(getContext(), "Please Install Email App", Toast.LENGTH_SHORT).show();
            }
        });

        instaContact.setOnClickListener(view -> {
            // Check if the Instagram app is installed on the device
            if (isInstagramAppInstalled()) {
                // Open Instagram app with the specific page (replace "username" with the desired username or "hashtag" with the desired hashtag)
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/MediVeda"));
                intent.setPackage("com.instagram.android");
                startActivity(intent);
            } else {
                // Instagram app is not installed, open Instagram web page in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/MediVeda"));
                startActivity(intent);
            }
        });

        twitterContact.setOnClickListener(view -> openTwitterProfile("MediVeda"));

        return v;
    }

    // Check if the Instagram app is installed on the device
    private boolean isInstagramAppInstalled() {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo("com.instagram.android", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }

    private void openTwitterProfile(String username) {
        try {
            // Check if the Twitter app is installed
            getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);

            // If Twitter app is installed, open the user's profile in the app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + username));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            // If Twitter app is not installed, open Twitter profile in a browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + username));
            startActivity(intent);
        }
    }

}