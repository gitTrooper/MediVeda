package com.saidev.MediVeda.adapters;




import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.saidev.MediVeda.R;
import com.saidev.MediVeda.modelClass.Hospital;


import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Hospital> mPlaceInfoList;


    public HospitalAdapter(Context mContext, List<Hospital> mPlaceInfoList) {
        this.mContext = mContext;
        this.mPlaceInfoList = mPlaceInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hospital_detail_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hospital placeInfo = mPlaceInfoList.get(position);
        holder.nameTextView.setText(placeInfo.getHospital_name());
        holder.ratingTextView.setText(placeInfo.getHospital_rating());
        holder.addressTextView.setText(placeInfo.getHospital_address());
        holder.detailCard.setOnClickListener(view -> {
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeInfo.getHospital_link()));

            // Check if there is an activity to handle the intent
            if (mapIntent.resolveActivity(holder.itemView.getContext().getPackageManager()) != null) {
                holder.itemView.getContext().startActivity(mapIntent); // Open the Maps app or a web browser with the location link
            } else {
                // Handle the case where there is no activity to handle the intent
                Toast.makeText(holder.itemView.getContext(), "No app can handle this action.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlaceInfoList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView ratingTextView, addressTextView;
        ConstraintLayout detailCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.hospital_name);
            ratingTextView = itemView.findViewById(R.id.hospital_rating);
            addressTextView = itemView.findViewById(R.id.hospital_address);
            detailCard = itemView.findViewById(R.id.hospitalDetailCard);
        }
    }
}