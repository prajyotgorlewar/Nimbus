package com.example.nimbus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.Collections;

public class CallUserAdapter extends RecyclerView.Adapter<CallUserViewHolder> {

    private final Context context;
    private final ArrayList<users> userList;

    public CallUserAdapter(Context context, ArrayList<users> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public CallUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CallUserViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user_call, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CallUserViewHolder holder, int position) {
        users user = userList.get(position);
        holder.username.setText(user.getUsername());



        holder.callButton.setOnClickListener(v -> {
            ZegoSendCallInvitationButton button = new ZegoSendCallInvitationButton(context);
            button.setIsVideoCall(true);
            button.setResourceID("zego_uikit_call"); // Ensure this is set up in ZEGOCLOUD Console
            button.setInvitees(Collections.singletonList(new ZegoUIKitUser(user.getUserId(), user.getUsername())));
            button.performClick(); // Triggers the call
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

