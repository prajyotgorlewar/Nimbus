package com.example.nimbus;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {

    public interface OnUserClick {
        void onClick(users user);
    }

    private Context context;
    private ArrayList<users> usersArrayList;
    private OnUserClick userClickCallback;

    public UserAdapter(Context context, ArrayList<users> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
        this.userClickCallback = userClickCallback;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {
        users user = usersArrayList.get(position);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getUserId())) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        }

        holder.username.setText(user.username);
        holder.userstatus.setText(user.status);

        try {
            int avatarSerial = Integer.parseInt(user.profilePic);
            int avatarResId = AvatarUtils.getAvatarResource(avatarSerial);
            holder.userImg.setImageResource(avatarResId);
        } catch (NumberFormatException e) {
            holder.userImg.setImageResource(R.drawable.avatar_1);
        }

        holder.itemView.setOnClickListener(v -> {
            if (userClickCallback != null) {
                userClickCallback.onClick(user);
            } else {
                // Fallback: Open ChatWindow
                Intent intent = new Intent(context, ChatWindow.class);
                intent.putExtra("name", user.getUsername());
                intent.putExtra("receiverImg", user.getProfilePic());
                intent.putExtra("uid", user.getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userImg;
        TextView username;
        TextView userstatus;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImg);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
        }
    }
}
