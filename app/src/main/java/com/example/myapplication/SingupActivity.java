package com.example.myapplication;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.graphics.text.TextRunShaper;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingupActivity extends AppCompatActivity {
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        TextView loginLinkTextView = findViewById(R.id.loginLinkTextView);
        String loginText = getString(R.string.already_have_an_account_nlogin_here);
        SpannableString spannableString = new SpannableString(loginText);
        int start = loginText.indexOf("Login here");
        int end = start + "Login here".length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Navigate to LoginActivity when clicked
                startActivity(new Intent(SingupActivity.this, LoginActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull android.text.TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#d8744c")); // Orange color
                ds.setUnderlineText(true); // Optional: underline the text
            }
        };

        spannableString.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginLinkTextView.setText(spannableString);
        loginLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signupButton);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Sign Up");
        dialog.setMessage("Signing up");
        dialog.setCanceledOnTouchOutside(false);



        signUpButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            dialog.show();

            if (!validateFields(username, email, password, confirmPassword)) {
                return;
            }

            createNewUser(username, email, password);
        });
    }

    private boolean validateFields(String username, String email, String password, String confirmPassword) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Z]).+$");

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username required");
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email required");
            return false;
        }

        if (TextUtils.isEmpty(password) || !passwordPattern.matcher(password).matches()) {
            passwordEditText.setError("Password must contain at least one capital letter");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void createNewUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid();  // Get the UID of the newly created user

                        // Create a HashMap or User object to store in Realtime Database
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("username", username);
                        userMap.put("email", email);

                        // Add the user data to Realtime Database under 'Users > users > UID'
                        mDatabase.child("users").child(userId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    // Navigate to LandingActivity after successful signup
                                    startActivity(new Intent(SingupActivity.this, LandingActivity.class));
                                    finish(); // Close signup activity
                                    Toast.makeText(SingupActivity.this, "User created and added to database", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(SingupActivity.this, "Failed to add user to database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(SingupActivity.this, "Failed to add user to database", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(SingupActivity.this, "Signup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class User {
        public String username;
        public String email;

        public User() {
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}