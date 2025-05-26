package com.example.nimbus;

import static com.example.nimbus.ChatWindow.receiverImage;
import static com.example.nimbus.ChatWindow.senderImage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<messageModel> messageAdapterArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;


    public messageAdapter(ArrayList<messageModel> messageAdapterArrayList, Context context) {
        this.messageAdapterArrayList = messageAdapterArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            Log.d("messageAdapter", "Creating sender view holder");
            return new senderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            Log.d("messageAdapter", "Creating receiver view holder");
            return new receiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        messageModel messages = messageAdapterArrayList.get(position);
        if (holder.getClass() == senderViewHolder.class) {
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.messageText.setText(messages.getMessage());

            try {
                int avatarSerial = Integer.parseInt(senderImage);
                int avatarResId = AvatarUtils.getAvatarResource(avatarSerial);
                viewHolder.circleImageView.setImageResource(avatarResId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                viewHolder.circleImageView.setImageResource(R.drawable.avatar_1);
            }

        } else {
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.messageText.setText(messages.getMessage());

            try {
                int avatarSerial = Integer.parseInt(receiverImage);
                int avatarResId = AvatarUtils.getAvatarResource(avatarSerial);
                viewHolder.circleImageView.setImageResource(avatarResId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                viewHolder.circleImageView.setImageResource(R.drawable.avatar_1);
            }

        }

    }

    @Override
    public int getItemCount() {
        return messageAdapterArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        messageModel messages = messageAdapterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.senderId)) {
            Log.d("ViewTypeCheck", "senderId: " + messages.getSenderId());
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
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

    class receiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView messageText;

        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.receiverProfileImg);
            messageText = itemView.findViewById(R.id.receiverMessageText);
        }
    }


}
