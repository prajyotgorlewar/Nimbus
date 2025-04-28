package com.example.nimbus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    private final List<Integer> avatarList;
    private final Context context;
    private OnAvatarSelectedListener listener;
    private int selectedPosition = 0;

    public AvatarAdapter(Context context, List<Integer> avatarList) {
        this.avatarList = avatarList;
        this.context = context;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.avatar_item, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition(); // Get the adapter position
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return; //check if the position is valid
        }

        int avatarSerial = avatarList.get(adapterPosition);
        int avatarImageResId = AvatarUtils.getAvatarResource(avatarSerial);

        holder.avatarImageView.setImageResource(avatarImageResId);

        if (adapterPosition == selectedPosition) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected_avatar_background));
        } else {
            holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAvatarSelected(avatarSerial);
                    selectedPosition = holder.getAdapterPosition(); // Use adapter position here too
                    notifyDataSetChanged();
                }
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
