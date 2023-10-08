package com.saidev.MediVeda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saidev.MediVeda.R;
import com.saidev.MediVeda.modelClass.Hospital;

import java.util.ArrayList;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {

    private List<Hospital> hospitals;

    public HospitalAdapter(List<Hospital> hospitals) {
        this.hospitals = new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_detail_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hospital hospital = hospitals.get(position);
        holder.nameTextView.setText(hospital.getName());
        holder.ratingTextView.setText(String.valueOf(hospital.getRating()));
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView ratingTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.hospital_name); // Replace with the actual ID of the TextView for the hospital name
            ratingTextView = itemView.findViewById(R.id.hospital_rating); // Replace with the actual ID of the TextView for the hospital rating
        }
    }
}
