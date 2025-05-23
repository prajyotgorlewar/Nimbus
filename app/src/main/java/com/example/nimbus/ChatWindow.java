package com.example.nimbus;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWindow extends AppCompatActivity {

    String receiverName, receiverUid, receiverImg, senderUid;
    CircleImageView profile;
    TextView receiverNameText;

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

        profile = findViewById(R.id.chatProfileImg);
        receiverNameText = findViewById(R.id.receivername);

        try {
            int avatarSerial = Integer.parseInt(receiverImg);
            int avatarResId = AvatarUtils.getAvatarResource(avatarSerial);
            profile.setImageResource(avatarResId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            profile.setImageResource(R.drawable.avatar_1);
        }

        receiverNameText.setText(""+receiverName);

    }
}