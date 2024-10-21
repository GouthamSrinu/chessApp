package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class ChessActivity extends AppCompatActivity {
    private androidx.gridlayout.widget.GridLayout chessBoard;
    private int gameTimer;
    private View[][] boardPieces; // Keep track of pieces on the board
    private View selectedPiece = null; // Keep track of selected piece
    private int selectedPieceRow = -1;
    private int selectedPieceCol = -1;
    private boolean isWhiteTurn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);
        isWhiteTurn=true;
        updateTurnIndicator();
        chessBoard = findViewById(R.id.chess_board);
        gameTimer = getIntent().getIntExtra("timer", 1);
        Toast.makeText(this, "Game timer set to: " + gameTimer + " minutes", Toast.LENGTH_SHORT).show();

        chessBoard.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                chessBoard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initializeChessBoard();
            }
        });

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> showSettingsPopup(v));
    }

    private void initializeChessBoard() {
        int[][] initialPiecePositions = {
                {R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_queen, R.drawable.white_king, R.drawable.white_bishop, R.drawable.white_knight, R.drawable.white_rook},
                {R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn, R.drawable.white_pawn},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn, R.drawable.black_pawn},
                {R.drawable.black_rook, R.drawable.black_knight, R.drawable.black_bishop, R.drawable.black_queen, R.drawable.black_king, R.drawable.black_bishop, R.drawable.black_knight, R.drawable.black_rook}
        };

        chessBoard.removeAllViews(); // Clear any previous views
        int rowCount = chessBoard.getRowCount();
        int columnCount = chessBoard.getColumnCount();

        boardPieces = new View[rowCount][columnCount]; // Initialize the boardPieces array

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                FrameLayout squareWithPiece = new FrameLayout(this);
                View square = new View(this);
                Log.d("ChessActivity", "resource Id: " + initialPiecePositions[row][col]);

                if ((row + col) % 2 == 0) {
                    squareWithPiece.setBackgroundColor(ContextCompat.getColor(this, R.color.chesswhite));
                } else {
                    squareWithPiece.setBackgroundColor(ContextCompat.getColor(this, R.color.chessblack));
                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.width = chessBoard.getWidth() / 8;
                params.height = chessBoard.getHeight() / 8;
                square.setLayoutParams(params);
                squareWithPiece.setLayoutParams(params);

                squareWithPiece.addView(square);

                int pieceResId = initialPiecePositions[row][col];
                if (pieceResId != 0) {
                    ImageView piece = new ImageView(this);
                    piece.setImageResource(pieceResId);
                    piece.setTag(pieceResId); // Tag to identify piece type

                    FrameLayout.LayoutParams pieceParams = new FrameLayout.LayoutParams(params.width, params.height);
                    pieceParams.gravity = Gravity.CENTER;
                    piece.setLayoutParams(pieceParams);

                    squareWithPiece.addView(piece);
                    boardPieces[row][col] = piece; // Store piece in the board array
                }

                int finalRow = row;
                int finalCol = col;
                squareWithPiece.setOnClickListener(v -> onSquareClick(finalRow, finalCol));
                chessBoard.addView(squareWithPiece);
            }
        }
    }

    // Handle clicking on a square
    private void onSquareClick(int targetX, int targetY) {
        if (selectedPiece == null) {
            // Select a piece if it exists at the clicked location
            selectedPiece = boardPieces[targetX][targetY];
            if (selectedPiece != null) {
                selectedPieceRow = targetX;
                selectedPieceCol = targetY;
                Toast.makeText(this, "Piece selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No piece at selected square", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Move the selected piece if it's a valid move
            movePiece(selectedPiece, selectedPieceRow, selectedPieceCol, targetX, targetY);
            selectedPiece = null; // Clear the selection after the move
        }
    }

    private boolean isValidMove(int pieceId, int startX, int startY, int targetX, int targetY, boolean isCapturing) {
        int[] wps = {R.drawable.white_pawn, R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_queen, R.drawable.white_king};

        int direction = (pieceId == R.drawable.white_pawn || pieceId == R.drawable.white_bishop || pieceId == R.drawable.white_king || pieceId == R.drawable.white_queen || pieceId == R.drawable.white_knight || pieceId == R.drawable.white_rook) ? 1 : -1; // Handle only pawns here for simplicity
        Log.d("ChessActivity", "Is valid move function called: " + isCapturing);

        // Check forward move (only if not capturing)
        boolean isValidForwardMove = (targetX == (startX + direction)) && (targetY == startY);
        if (isValidForwardMove) {
            Log.d("ChessActivity", "Valid forward move detected.");
            return true;
        }

        // Check diagonal capture
        boolean isValidDiagonalCapture = (targetX == (startX + direction)) && (Math.abs(targetY - startY) == 1) && isCapturing;
        if (isValidDiagonalCapture) {
            Log.d("ChessActivity", "Valid diagonal capture detected.");
            return true;
        }

        Log.d("ChessActivity", "Invalid move.");
        return false; // No valid move found
    }
    private boolean isWhitePiece(int pieceId) {
        int[] whitePieces = {
                R.drawable.white_pawn, R.drawable.white_rook, R.drawable.white_knight,
                R.drawable.white_bishop, R.drawable.white_queen, R.drawable.white_king
        };
        return Arrays.asList(whitePieces).contains(pieceId);
    }

    private boolean isBlackPiece(int pieceId) {
        int[] blackPieces = {
                R.drawable.black_pawn, R.drawable.black_rook, R.drawable.black_knight,
                R.drawable.black_bishop, R.drawable.black_queen, R.drawable.black_king
        };
        return Arrays.asList(blackPieces).contains(pieceId);
    }

    private void movePiece(View piece, int startX, int startY, int targetX, int targetY) {
        boolean isCapturing = isOpponentPieceAt(targetX, targetY, piece);
        Log.d("ChessActivity", "Attempting to move piece. Is capturing: " + isCapturing);
        int pieceId = (Integer) piece.getTag();
         // Get the existing piece
        Log.d("ChessActivity", "current Piece " + isWhitePiece(pieceId));


        // Check if the selected piece belongs to the current player
        boolean isPlayerPiece = (isWhiteTurn && isWhitePiece(pieceId)) || (!isWhiteTurn && isBlackPiece(pieceId));
        Log.d("ChessActivity", "isPlayerPiece: " + isPlayerPiece);

//        if (!isPlayerPiece) {
//            Toast.makeText(this, "It's not your turn!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Check if the move is valid
        boolean isValid = isValidMove(pieceId, startX, startY, targetX, targetY, isCapturing);
        Log.d("ChessActivity", "Attempting to move piece. Is valid: " + isValid);

        if (isValid) {
            // Check if we are capturing an opponent's piece
            if (isCapturing) {
                removeOpponentPieceAt(targetX, targetY); // Remove captured piece
            }

            // Update the position of the piece
            boardPieces[startX][startY] = null; // Clear the old position in the board array
            updatePiecePosition(piece, targetX, targetY); // Move the piece visually
            boardPieces[targetX][targetY] = piece;

            isWhiteTurn = !isWhiteTurn; // Switch turn
            updateTurnIndicator();
        } else {
            Toast.makeText(this, "Invalid Move", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePiecePosition(View piece, int targetX, int targetY) {
        // First, clear the position in the board array

        boardPieces[targetX][targetY] = piece; // Place the piece at the new location
        Log.d("ChessActivity", "updatePiecePosition() move function called");
        if (piece.getParent() != null) {
            ((ViewGroup) piece.getParent()).removeView(piece);
        }
        // Update the layout parameters to move the piece visually
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                chessBoard.getWidth() / 8,
                chessBoard.getHeight() / 8
        );
        params.gravity = Gravity.CENTER;

        // Find the square where the piece is moving to
        View targetSquare = chessBoard.getChildAt(targetX * chessBoard.getColumnCount() + targetY);

        // Remove the piece from its old position and add it to the new one
        ((FrameLayout) targetSquare).addView(piece, params);  // Add the piece to the target square
    }

    private boolean isOpponentPieceAt(int targetX, int targetY, View currentPiece) {
        View targetPiece = boardPieces[targetX][targetY];
        boolean isOpponent = false;
        if (targetPiece != null) {
            int targetPieceId = (Integer) targetPiece.getTag(); // Get the target piece ID
            int currentPieceId = (Integer) currentPiece.getTag(); // Get the current piece ID
            isOpponent = isOpponentPiece(currentPieceId, targetPieceId); // Pass both IDs to check
            Log.d("ChessActivity", "Checking opponent piece at: " + targetX + ", " + targetY + " - Is opponent: " + isOpponent);
        }
        return isOpponent; // Returns false if no piece is present
    }
    private void updateTurnIndicator() {
        String currentPlayer = isWhiteTurn ? "White's Turn" : "Black's Turn";
        Toast.makeText(this, currentPlayer, Toast.LENGTH_SHORT).show();
        // Alternatively, update a TextView in your UI if you have one
    }

    private boolean isOpponentPiece(int currentPieceId, int targetPieceId) {
        int[] wps = {R.drawable.white_pawn, R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_queen, R.drawable.white_king};
        int[] bps = {R.drawable.black_pawn, R.drawable.black_rook, R.drawable.black_knight, R.drawable.black_bishop, R.drawable.black_queen, R.drawable.black_king};

        boolean cisWhite = Arrays.asList(wps)
                .contains(currentPieceId);
        boolean cisBlack = Arrays.asList(bps)
                .contains(currentPieceId);
        boolean tisWhite = Arrays.asList(wps)
                .contains(targetPieceId);
        boolean tisBlack = Arrays.asList(bps)
                .contains(targetPieceId);
        if(cisWhite&&tisWhite||cisBlack&&tisBlack)
            return false;
        return true;
    }

    private void removeOpponentPieceAt(int targetX, int targetY) {
        View targetPiece = boardPieces[targetX][targetY];
        Log.d("ChessActivity", "Removing opponent at"+targetX+", "+targetY);// Get the opponent piece
        if (targetPiece != null) {
            targetPiece.setVisibility(View.GONE); // Hide the opponent piece
            boardPieces[targetX][targetY] = null; // Remove from board array
        }
    }

    private void showSettingsPopup(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_settings, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.showAsDropDown(view);
    }
}