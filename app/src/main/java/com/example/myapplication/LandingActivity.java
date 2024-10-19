package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LandingActivity extends AppCompatActivity {

    private ImageButton profileButton;
    private ImageButton settingsButton;
    private ToggleButton oneMinButton;
    private ToggleButton threeMinButton;
    private ToggleButton moreButton;
    private int selectedTime = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Initialize buttons
        profileButton = findViewById(R.id.profileButton);
        settingsButton = findViewById(R.id.settingsButton);
        Button btnMore = findViewById(R.id.moreButton);
        Button btnPlay = findViewById(R.id.btnPlay);
        //timerTextView = findViewById(R.id.timerTextView);
        oneMinButton = findViewById(R.id.oneMinButton);
        threeMinButton = findViewById(R.id.threeMinButton);
        moreButton = findViewById(R.id.moreButton);
        oneMinButton.setBackgroundResource(R.drawable.text_border);
        threeMinButton.setBackgroundResource(R.drawable.text_border);
        moreButton.setBackgroundResource(R.drawable.text_border);

        oneMinButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedTime = 1; // 1 minute selected
                threeMinButton.setChecked(false); // Deselect other buttons
                moreButton.setChecked(false);
                oneMinButton.setBackgroundResource(R.drawable.button_background);
            }else{
                oneMinButton.setBackgroundResource(R.drawable.text_border);
            }
        });

        threeMinButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedTime = 3; // 3 minutes selected
                oneMinButton.setChecked(false); // Deselect other buttons
                moreButton.setChecked(false);
                threeMinButton.setBackgroundResource(R.drawable.button_background);
            }else{
                threeMinButton.setBackgroundResource(R.drawable.text_border);
            }
        });

        moreButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Assuming "More" means 5 minutes for this example
                selectedTime = 5; // 5 minutes selected
                oneMinButton.setChecked(false); // Deselect other buttons
                threeMinButton.setChecked(false);
                moreButton.setBackgroundResource(R.drawable.button_background);
            }else{
                moreButton.setBackgroundResource(R.drawable.text_border);
            }
        });
        btnMore.setOnClickListener(v -> showTimePickerDialog());

        btnPlay.setOnClickListener(v -> {
            // Navigate to ChessActivity with the selected timer value
            Intent intent = new Intent(LandingActivity.this, ChessActivity.class);
            intent.putExtra("timer", selectedTime);  // Passing the selected time to ChessActivity
            startActivity(intent);
        });

        // Set profile button click listener
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, PersonActivity.class);
            startActivity(intent);
        });

        // Set settings button click listener
        settingsButton.setOnClickListener(v -> {
            SettingsDialogFragment settingsDialog = new SettingsDialogFragment();
            settingsDialog.show(getSupportFragmentManager(), "SettingsDialog");
        });
    }
    private void showTimePickerDialog() {
        // Define a list of options for the user to select the timer value
        final String[] timeOptions = {"1 min", "2 min", "3 min", "4 min", "5 min"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Time");
        builder.setItems(timeOptions, (dialog, which) -> {
            // Update the selected time based on the user's choice
            selectedTime = which + 1;  // 'which' is the index, add 1 to get the actual time
            //timerTextView.setText("Selected Time: " + timeOptions[which]);
            Toast.makeText(LandingActivity.this, "Time set to: " + timeOptions[which], Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
}