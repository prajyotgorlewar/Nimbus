package com.example.nimbus;

import android.app.Activity;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class register extends AppCompatActivity {

    TextView loginBtn;
    EditText rgUsername, rgEmail, rgPassword, rgConfirmPassword;
    Button rgSignup;
    CircleImageView rgProfileImg;
    FirebaseAuth auth;
    Uri imageURI;
    String imageuri;
    String emailPattern = "[a-zA-Z0-9.-]+(.[a-zA-Z]{2,})+";
    FirebaseDatabase database;
    private ActivityResultLauncher<Intent> imageChooserLauncher;
    private Uri selectedImageUri;


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

        imageChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                selectedImageUri = data.getData();
                                // Load the selected image into your ImageView
                                rgProfileImg.setImageURI(selectedImageUri);
                                // Now you have the URI of the selected image (selectedImageUri)
                                // You can proceed to upload this to Firebase Storage later
                                // when the user clicks the signup button.
                            }
                        }
                    }
                });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(register.this, login.class);
                startActivity(intent);
                finish();
            }
        });

        rgProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        rgSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = rgUsername.getText().toString();
                String email = rgEmail.getText().toString();
                String password = rgPassword.getText().toString();
                String cpassword = rgConfirmPassword.getText().toString();
                String status = "Hey I'm using Nimbus";

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                        || TextUtils.isEmpty(password) || TextUtils.isEmpty(cpassword)) {
                    Toast.makeText(register.this, "Enter Valid Information", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)) {
                    rgEmail.setError("Enter Valid Email Id");
                } else if (!(rgPassword.length() < 8)) {
                    rgPassword.setError("Password too short");
                } else if (!password.equals(cpassword)) {
                    rgConfirmPassword.setError("The Password doesn't match! ");
                } else {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    String id = task.getResult().getUser().getUid();
                                    DatabaseReference userReference = database.getReference().child("users").child(id);
                                    int selectedAvatarSerial = getSelectedAvatarSerial();
                                    users newUser = new users(id,name,email,password,selectedAvatarSerial,status);

                                    userReference.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                                  Intent intent = new Intent(register.this, MainActivity.class);
                                                  startActivity(intent);
                                                  finish();
                                            } else {
                                                Toast.makeText(register.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(register.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10)
        {
            if(data!=null)
            {
                imageURI = data.getData();
                rgProfileImg.setImageURI(imageURI);
            }
        }
    }

    private int getSelectedAvatarSerial() {
        return 1;
    }
}