package com.example.nimbus;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class settings extends AppCompatActivity {

    ImageView setProfile;
    TextView setName, setStatus;
    Button saveBtn;
    FirebaseDatabase database;
    FirebaseAuth auth;
    String email, password;
    int selectedAvatarSerial = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        setProfile = findViewById(R.id.settingProfile);
        setName = findViewById(R.id.settingName);
        setStatus = findViewById(R.id.settingStatus);
        saveBtn = findViewById(R.id.saveBtn);

        // Load user data
        DatabaseReference reference = database.getReference().child("users").child(auth.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("mail").getValue().toString();
                password = snapshot.child("password").getValue().toString();
                String name = snapshot.child("username").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String profile = snapshot.child("profilePic").getValue().toString();
                setName.setText(name);
                setStatus.setText(status);

                try {
                    selectedAvatarSerial = Integer.parseInt(profile);
                    int avatarResId = AvatarUtils.getAvatarResource(selectedAvatarSerial);
                    setProfile.setImageResource(avatarResId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    setProfile.setImageResource(R.drawable.avatar_1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Open avatar selector dialog
        setProfile.setOnClickListener(v -> showAvatarSelectorDialog());

        // Save updated data
        saveBtn.setOnClickListener(v -> {
            String name = setName.getText().toString().trim();
            String status = setStatus.getText().toString().trim();

            DatabaseReference userRef = database.getReference().child("users").child(auth.getUid());
            userRef.child("username").setValue(name);
            userRef.child("status").setValue(status);
            userRef.child("profilePic").setValue(String.valueOf(selectedAvatarSerial));

            Toast.makeText(settings.this, "Profile Updated", Toast.LENGTH_SHORT).show();
        });
    }

    private void showAvatarSelectorDialog() {
        Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.avatar_selector, null);
        dialog.setContentView(dialogView);

        RecyclerView avatarRecyclerView = dialogView.findViewById(R.id.dialogAvatarRecyclerView);
        avatarRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Integer> avatarList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            avatarList.add(i);
        }

        AvatarAdapter avatarAdapter = new AvatarAdapter(this, avatarList);
        avatarRecyclerView.setAdapter(avatarAdapter);

        avatarAdapter.setOnAvatarSelectedListener(avatarSerial -> {
            selectedAvatarSerial = avatarSerial;
            int avatarResId = AvatarUtils.getAvatarResource(avatarSerial);
            setProfile.setImageResource(avatarResId);
            dialog.dismiss();
        });

        dialog.show();
    }
}
