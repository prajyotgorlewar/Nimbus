package com.example.nimbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class register extends AppCompatActivity {

    TextView loginBtn;
    EditText rgUsername, rgEmail, rgPassword, rgConfirmPassword;
    Button rgSignup;
    CircleImageView rgProfileImg;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private ActivityResultLauncher<Intent> imageChooserLauncher;
    private Uri selectedImageUri;
    private int selectedAvatarSerial = 1;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginBtn = findViewById(R.id.rgLogin);
        rgUsername = findViewById(R.id.rgUsername);
        rgEmail = findViewById(R.id.rgEmail);
        rgPassword = findViewById(R.id.rgPassword);
        rgConfirmPassword = findViewById(R.id.rgConfirmPassword);
        rgSignup = findViewById(R.id.rgSignup);
        rgProfileImg = findViewById(R.id.rgProfileImg);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(false);

        imageChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                selectedImageUri = data.getData();
                            }
                        }
                    }
                });

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(register.this, login.class);
            startActivity(intent);
            finish();
        });

        rgProfileImg.setOnClickListener(v -> showAvatarSelectionDialog());

        rgSignup.setOnClickListener(v -> {
            String name = rgUsername.getText().toString();
            String email = rgEmail.getText().toString();
            String password = rgPassword.getText().toString();
            String cpassword = rgConfirmPassword.getText().toString();
            String status = "Hey I'm using Nimbus";

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                    || TextUtils.isEmpty(password) || TextUtils.isEmpty(cpassword)) {
                progressDialog.dismiss();
                Toast.makeText(register.this, "Enter Valid Information", Toast.LENGTH_SHORT).show();
            } else if (!email.matches(emailPattern)) {
                progressDialog.dismiss();
                rgEmail.setError("Enter Valid Email Id");
            } else if (!(rgPassword.length() >= 8)) {
                progressDialog.dismiss();
                rgPassword.setError("Password too short");
            } else if (!password.equals(cpassword)) {
                progressDialog.dismiss();
                rgConfirmPassword.setError("The Password doesn't match! ");
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String id = user.getUid();
                            DatabaseReference userReference = database.getReference().child("users").child(id);
                            users newUser = new users(id, name, email, password, String.valueOf(selectedAvatarSerial), status);
                            userReference.setValue(newUser).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    progressDialog.show();
                                    Toast.makeText(register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(register.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(register.this, "Failed to save user data: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(register.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showAvatarSelectionDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.avatar_selector, null);
        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.dialogAvatarRecyclerView);
        dialogRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Integer> avatarList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            avatarList.add(i);
        }

        AvatarAdapter dialogAdapter = new AvatarAdapter(this, avatarList);
        dialogRecyclerView.setAdapter(dialogAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogAdapter.setOnAvatarSelectedListener(avatarSerial -> {
            selectedAvatarSerial = avatarSerial;
            int avatarImageResId = AvatarUtils.getAvatarResource(avatarSerial);
            rgProfileImg.setImageResource(avatarImageResId);
            dialog.dismiss();
        });

        dialog.show();
    }
}
