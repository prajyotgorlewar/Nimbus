package com.example.nimbus;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class calling extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<users> userList = new ArrayList<>();
    private CallUserAdapter callUserAdapter;

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calling);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        callUserAdapter = new CallUserAdapter(this, userList);
        recyclerView.setAdapter(callUserAdapter);

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String currentUid = auth.getCurrentUser().getUid();

        userRef.child(currentUid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentUsername = snapshot.getValue(String.class);
                if (currentUsername == null) {
                    Toast.makeText(calling.this, "Username not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    long appId = Long.parseLong(getString(R.string.app_id));
                    String appSign = getString(R.string.app_sign);

                    ZegoCallManager.initialize(getApplicationContext(), appId, appSign, currentUid, currentUsername);
                    loadUsersFromFirebase(currentUid);

                } catch (NumberFormatException e) {
                    Toast.makeText(calling.this, "Invalid app ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(calling.this, "Error fetching username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsersFromFirebase(String currentUid) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    users user = userSnap.getValue(users.class);
                    if (user != null && !user.getUserId().equals(currentUid)) {
                        userList.add(user);
                    }
                }
                callUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(calling.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoCallManager.uninitialize();
    }
}
