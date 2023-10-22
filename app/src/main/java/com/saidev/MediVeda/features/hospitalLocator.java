package com.saidev.MediVeda.features;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.saidev.MediVeda.R;
import com.saidev.MediVeda.adapters.HospitalAdapter;
import com.saidev.MediVeda.modelClass.Hospital;

import java.util.ArrayList;
import java.util.List;

public class hospitalLocator extends AppCompatActivity {

    Button selectCity;

    private RecyclerView recyclerView;
    private HospitalAdapter adapter;
    private List<Hospital> dataList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_locator);

        selectCity = findViewById(R.id.city_button);
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.hospitalDetailRecycler);
        dataList = new ArrayList<>();

        adapter = new HospitalAdapter(this, dataList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        selectCity.setOnClickListener(view -> {
            // Create a PopupMenu
            PopupMenu popupMenu = new PopupMenu(hospitalLocator.this, selectCity);
            popupMenu.getMenuInflater().inflate(R.menu.city_menu, popupMenu.getMenu());

            // Set item click listener
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.prayagraj:
                        fetchDataFromFirestore("Prayagraj");
                        selectCity.setVisibility(View.GONE);
                        return true;

                    case R.id.kanpur:
                        fetchDataFromFirestore("Kanpur");
                        selectCity.setVisibility(View.GONE);
                        return true;

                    case R.id.lucknow:
                        fetchDataFromFirestore("Lucknow");
                        selectCity.setVisibility(View.GONE);
                        return true;



                    case R.id.bangalore:
                        fetchDataFromFirestore("Bangalore");
                        selectCity.setVisibility(View.GONE);
                        return true;

                    default:
                        return false;
                }
            });

            // Show the PopupMenu
            popupMenu.show();
        });
    }


    private void fetchDataFromFirestore(String city) {
        // Reference to the subcollection under the main document
        CollectionReference subCollectionRef = db.collection("Hospital Data")
                .document(city)
                .collection("List Of Hospitals");

        subCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    // Convert the document to YourModel class
                    Hospital model = documentSnapshot.toObject(Hospital.class);
                        dataList.add(model);
                }
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Toast.makeText(hospitalLocator.this, e.toString(), Toast.LENGTH_SHORT).show());
    }


}
