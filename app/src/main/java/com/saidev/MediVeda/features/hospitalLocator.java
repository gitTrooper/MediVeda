package com.saidev.MediVeda.features;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.saidev.MediVeda.R;
import com.saidev.MediVeda.adapters.HospitalAdapter;
import com.saidev.MediVeda.modelClass.Hospital;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class hospitalLocator extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RecyclerView recyclerView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_locator);

        recyclerView = findViewById(R.id.hospital_recyclerView);

        // Check if the app has location permission, request it if not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed to get location and display nearby hospitals
            // Call your method to get location and display hospitals here
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Log.e("user ki location", location.toString());
                        Toast.makeText(hospitalLocator.this, location.toString(), Toast.LENGTH_SHORT).show();
                        // Use the obtained location here.
                        // The user has granted the permission.
                        // Make the Google Places API request.
                        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                                "location=" + location.getLatitude() + "," + location.getLongitude() + "&" +
                                "radius=5000&" +
                                "type=hospital&" +
                                "key=AIzaSyAOEeTVganvMYirqYrBXk-s-DBMIY9bs9Y";

                        Volley.newRequestQueue(getApplicationContext()).add(new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Parse the Google Places API response and populate the RecyclerView with the results.
                                List<Hospital> hospitals = new ArrayList<>();
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(response);
                                    JSONArray results = jsonObject.getJSONArray("results");
                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject hospitalJson = results.getJSONObject(i);
                                        String name = hospitalJson.getString("name");
                                        double rating = hospitalJson.getDouble("rating");

                                        Hospital hospital = new Hospital(name, rating);
                                        hospitals.add(hospital);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                // Create a RecyclerView adapter and set it to the RecyclerView.
                                HospitalAdapter adapter = new HospitalAdapter(hospitals);
                                recyclerView.setAdapter(adapter);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle the error.
                                Toast.makeText(hospitalLocator.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }));

                    }else {
                        Toast.makeText(hospitalLocator.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Request the ACCESS_FINE_LOCATION permission.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    Log.e("user ki location", location.toString());
                    Toast.makeText(hospitalLocator.this, location.toString(), Toast.LENGTH_SHORT).show();
                    if (location != null) {
                        // Use the obtained location here.
                        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
                            Toast.makeText(getApplicationContext(), "working", Toast.LENGTH_SHORT).show();
                            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                // The user has granted the permission.
                                // Make the Google Places API request.

                                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                                        "location=" + location.getLatitude() + "," + location.getLongitude() + "&" +
                                        "radius=5000&" +
                                        "type=hospital&" +
                                        "key=AIzaSyAOEeTVganvMYirqYrBXk-s-DBMIY9bs9Y";

                                Volley.newRequestQueue(getApplicationContext()).add(new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Parse the Google Places API response and populate the RecyclerView with the results.
                                        List<Hospital> hospitals = new ArrayList<>();
                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(response);
                                            JSONArray results = jsonObject.getJSONArray("results");
                                            for (int i = 0; i < results.length(); i++) {
                                                JSONObject hospitalJson = results.getJSONObject(i);
                                                String name = hospitalJson.getString("name");
                                                double rating = hospitalJson.getDouble("rating");

                                                Hospital hospital = new Hospital(name, rating);
                                                hospitals.add(hospital);
                                            }
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }

                                        // Create a RecyclerView adapter and set it to the RecyclerView.
                                        HospitalAdapter adapter = new HospitalAdapter(hospitals);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Handle the error.
                                        Toast.makeText(hospitalLocator.this, error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }));
                            }
                        }
                    } else {
                        Toast.makeText(hospitalLocator.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }

    }


}