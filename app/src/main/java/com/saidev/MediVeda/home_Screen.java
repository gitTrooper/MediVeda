package com.saidev.MediVeda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saidev.MediVeda.menuItemFragments.contactFragment;
import com.saidev.MediVeda.menuItemFragments.homeScreen;
import com.saidev.MediVeda.menuItemFragments.settingFragment;

public class home_Screen extends AppCompatActivity {

    String appPackageName = "com.saidev.MediVeda";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        toolbar = findViewById(R.id.home_toolbar);
        userName = headerView.findViewById(R.id.user_name);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState==null){
            replaceFragment(new homeScreen());
            navigationView.setCheckedItem(R.id.drawer_home);
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String documentId = user.getEmail(); // Replace with the actual document ID
        DocumentReference userDocumentRef = db.collection("Users").document(documentId);
        userDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, you can access its data
                        userName.setText(document.getString("UserName"));

                        // Do something with the data
                    }
                }

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.drawer_home:
                        //
                        replaceFragment(new homeScreen());
                        break;

                    case R.id.drawer_contact:
                        //
                        replaceFragment(new contactFragment());
                        break;

                    case R.id.drawer_setting:
                        //
                        replaceFragment(new settingFragment());
                        break;

                    case R.id.drawer_rate_us:
                        //
                        try {
                            // Open the Play Store page of your app
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (ActivityNotFoundException e) {
                            // If the Play Store app is not installed, open the Play Store website
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        break;

                    case R.id.drawer_logout:
                        mAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), login_screen.class));
                        finish();
                        break;

                    case R.id.drawer_social:
                        //
                        break;

                    case R.id.drawer_share:
                        // Create the share message
                        String message = "Discover personalized healthcare with Mediveda! \uD83C\uDFE5 Assess your prakruti, diagnose diseases, and book appointments at nearby hospitals seamlessly. Stay informed about real-time emergency medical facilities nearby. Your health, our priority. Download now: [https://play.google.com/store/apps/details?id=com.saidev.MediVeda] \uD83C\uDF1F #Healthcare #Mediveda";
                        // Create a new Intent with ACTION_SEND to share the message
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                        // Start the chooser to let the user pick an app for sharing
                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.home_content, fragment);
        transaction.commit();
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}