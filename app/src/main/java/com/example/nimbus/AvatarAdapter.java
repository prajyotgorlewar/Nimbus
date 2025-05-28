package com.example.nimbus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    private final List<Integer> avatarList;
    private final Context context;
    private OnAvatarSelectedListener listener;
    private int selectedPosition = 0;

    public AvatarAdapter(Context context, List<Integer> avatarList) {
        this.context = context;
        this.avatarList = avatarList;
    }

    public interface OnAvatarSelectedListener {
        void onAvatarSelected(int avatarSerial);
    }

    public void setOnAvatarSelectedListener(OnAvatarSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.avatar_item, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        int avatarSerial = avatarList.get(position);
        int avatarImageResId = AvatarUtils.getAvatarResource(avatarSerial);
        holder.avatarImageView.setImageResource(avatarImageResId);

        // Highlight selected avatar with border color
        if (position == selectedPosition) {
            holder.avatarImageView.setBorderColor(
                    ContextCompat.getColor(context, R.color.selected_avatar_border));
            holder.avatarImageView.setBorderWidth(6);
        } else {
            holder.avatarImageView.setBorderColor(
                    ContextCompat.getColor(context, android.R.color.transparent));
            holder.avatarImageView.setBorderWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                listener.onAvatarSelected(avatarSerial);
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    public static class AvatarViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatarImageView;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
