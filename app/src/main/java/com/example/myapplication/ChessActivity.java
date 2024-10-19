package com.example.myapplication;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout.LayoutParams;

public class ChessActivity extends AppCompatActivity {

    private androidx.gridlayout.widget.GridLayout chessBoard;
    private int gameTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        chessBoard = findViewById(R.id.chess_board);
        gameTimer = getIntent().getIntExtra("timer", 1);
        Toast.makeText(this, "Game timer set to: " + gameTimer + " minutes", Toast.LENGTH_SHORT).show();

        // Wait for the layout to be fully measured
        chessBoard.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent multiple calls
                chessBoard.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Initialize chessboard only after layout has been measured
                initializeChessBoard();
            }
        });

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> showSettingsPopup(v));
    }

    private void initializeChessBoard() {
        int[][] initialPiecePositions = {
                {R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_queen, R.drawable.white_king, R.drawable.white_bishop, R.drawable.white_knight, R.drawable.white_rook}, // Row 0
                {R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn}, // Row 1
                {0, 0, 0, 0, 0, 0, 0, 0}, // Row 2
                {0, 0, 0, 0, 0, 0, 0, 0}, // Row 3
                {0, 0, 0, 0, 0, 0, 0, 0}, // Row 4
                {0, 0, 0, 0, 0, 0, 0, 0}, // Row 5
                {R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn}, // Row 6
                {R.drawable.black_rook, R.drawable.black_knight, R.drawable.black_bishop, R.drawable.black_queen, R.drawable.black_king, R.drawable.black_bishop, R.drawable.black_knight, R.drawable.black_rook} // Row 7
        };

        chessBoard.removeAllViews(); // Clear existing views

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                View square = new View(this);
                // Set alternating colors for the squares
                if ((row + col) % 2 == 0) {
                    square.setBackgroundColor(ContextCompat.getColor(this, R.color.chesswhite));
                } else {
                    square.setBackgroundColor(ContextCompat.getColor(this, R.color.chessblack));
                }

                // Set the size of each square
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.width = chessBoard.getWidth() / 8; // Each square takes an equal portion of the grid's width
                params.height = chessBoard.getHeight() / 8; // Each square takes an equal portion of the grid's height
                square.setLayoutParams(params);

                // Add square to the chess board
                chessBoard.addView(square);

                // Add pieces based on initialPiecePositions

            }
        }
    }


    private void showSettingsPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_settings, null);

        PopupWindow popupWindow = new PopupWindow(popupView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        // Handle close button
        ImageButton closeSettings = popupView.findViewById(R.id.closeSettings);
        closeSettings.setOnClickListener(v -> popupWindow.dismiss());
    }
}