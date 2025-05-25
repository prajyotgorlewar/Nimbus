package com.example.nimbus;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWindow extends AppCompatActivity {

    String receiverName, receiverUid, receiverImg, senderUid;
    CircleImageView profile;
    TextView receiverNameText;
    CardView sendBtn;
    EditText messageText;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    public static String senderImage;
    public static String receiverImage;
    String senderRoom, receiverRoom;
    RecyclerView messageRecycler;
    ArrayList<messageModel> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_window);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        receiverName = getIntent().getStringExtra("name");
        receiverUid = getIntent().getStringExtra("uid");
        receiverImg = getIntent().getStringExtra("receiverImg");

        messageArrayList = new ArrayList<>();

        messageRecycler = findViewById(R.id.messageRecycler);
        profile = findViewById(R.id.chatProfileImg);
        receiverNameText = findViewById(R.id.receivername);
        messageText = findViewById(R.id.messageText);
        sendBtn = findViewById(R.id.sendBtn);

        try {
            int avatarSerial = Integer.parseInt(receiverImg);
            int avatarResId = AvatarUtils.getAvatarResource(avatarSerial);
            profile.setImageResource(avatarResId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            profile.setImageResource(R.drawable.avatar_1);
        }

        receiverNameText.setText("" + receiverName);
        DatabaseReference reference = database.getReference().child("users").child(firebaseAuth.getUid());
        DatabaseReference chatReference = database.getReference().child("users").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshots : snapshot.getChildren()) {
                    messageModel messages = dataSnapshots.getValue(messageModel.class);
                    messageArrayList.add(messages);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImage = snapshot.child("profilePic").getValue().toString();
                receiverImage = receiverImg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderUid = firebaseAuth.getUid();
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatWindow.this, "Type something first!", Toast.LENGTH_SHORT).show();
                }
                messageText.setText("");
                Date date = new Date();
                messageModel messageModel = new messageModel(message, senderUid, date.getTime());
                database = FirebaseDatabase.getInstance();
                database.getReference().child("chats").child("senderRoom").child("messages").push().setValue(messageModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("receiverRoom").child("messages").push().setValue(messageModel)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });

    }
}