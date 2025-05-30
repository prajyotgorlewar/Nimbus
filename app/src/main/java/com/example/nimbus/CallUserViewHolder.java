package com.example.nimbus;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class CallUserViewHolder extends RecyclerView.ViewHolder {

    CircleImageView userImg;
    TextView username;
    ZegoSendCallInvitationButton callButton;

    public CallUserViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.usernameTextView);
        callButton = itemView.findViewById(R.id.callButton);
    }
}
