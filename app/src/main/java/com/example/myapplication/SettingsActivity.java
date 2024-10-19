package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private Button logoutButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize buttons
        logoutButton = findViewById(R.id.logoutButton);
        exitButton = findViewById(R.id.exitButton);

        // Set logout button click listener
        logoutButton.setOnClickListener(v -> {
            // Implement logout logic here
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Close current activity
        });

        // Set exit button click listener
        exitButton.setOnClickListener(v -> {
            finishAffinity();  // Exit the application
        });
    }
}