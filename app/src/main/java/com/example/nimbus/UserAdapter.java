package com.example.nimbus;

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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder>{

    MainActivity mainActivity;
    ArrayList<users> usersArrayList;

    public UserAdapter(MainActivity mainActivity, ArrayList<users> usersArrayList) {
        this.mainActivity = mainActivity;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {
        users user = usersArrayList.get(position);

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.getUserId())){
            holder.itemView.setVisibility(View.GONE);
        }

        holder.username.setText(user.username);
        holder.userstatus.setText(user.status);

        try {
            int avatarSerial = Integer.parseInt(user.profilePic);
            int avatarResId = AvatarUtils.getAvatarResource(avatarSerial);
            holder.userImg.setImageResource(avatarResId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            holder.userImg.setImageResource(R.drawable.avatar_1);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mainActivity, ChatWindow.class);
                Intent intent = new Intent(v.getContext(), ChatWindow.class);
                intent.putExtra("name", user.getUsername());
                intent.putExtra("receiverImg",user.getProfilePic());
                intent.putExtra("uid", user.getUserId());
                mainActivity.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
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
