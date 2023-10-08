package com.saidev.MediVeda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.MyViewHolder> {

    List<message> messageList;

    public messageAdapter(List<message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_txt_holder, parent, false);

        return new MyViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        message msg = messageList.get(position);
        if (msg.getSentBy().equals(message.SENT_BY_USER)){
            holder.botMessage.setVisibility(View.GONE);
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.userTextMessage.setText(msg.getMessage());
        }
        else {
            holder.userMessage.setVisibility(View.GONE);
            holder.botMessage.setVisibility(View.VISIBLE);
            holder.botTextMessage.setText(msg.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout userMessage, botMessage;
        TextView userTextMessage, botTextMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.user_message);
            botMessage  = itemView.findViewById(R.id.bot_message);
            userTextMessage = itemView.findViewById(R.id.user_text_message);
            botTextMessage = itemView.findViewById(R.id.bot_text_message);
        }
    }

}
