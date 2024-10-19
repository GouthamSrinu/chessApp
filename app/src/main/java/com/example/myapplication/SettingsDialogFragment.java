package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SettingsDialogFragment extends DialogFragment {

    private Button logoutButton;
    private Button exitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_settings, container, false);

        // Initialize buttons
        logoutButton = view.findViewById(R.id.logoutButton);
        exitButton = view.findViewById(R.id.exitButton);

        // Set logout button click listener
        logoutButton.setOnClickListener(v -> {
            // Implement logout logic here
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            dismiss(); // Close the dialog
        });

        // Set exit button click listener
        exitButton.setOnClickListener(v -> {
            dismiss(); // Close the dialog
            getActivity().finishAffinity(); // Exit the application
        });

        return view;
    }
}
