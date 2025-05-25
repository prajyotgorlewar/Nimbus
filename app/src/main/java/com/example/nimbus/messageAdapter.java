package com.example.nimbus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<messageModel> messageAdapterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECEIVE = 2;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new senderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class senderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView messageText;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.senderProfileImg);
            messageText = itemView.findViewById(R.id.senderMessageText);
        }
    }

    class receiverVeiwHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView messageText;
        public receiverVeiwHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.receiverProfileImg);
            messageText = itemView.findViewById(R.id.receiverMessageText);
        }
    }


}
