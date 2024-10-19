package com.example.myapplication;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.gridlayout.widget.GridLayout;

public class ChessPieceView extends androidx.appcompat.widget.AppCompatImageView {

    private float downX, downY;
    private GridLayout chessBoard;
    private Point originalPosition;

    public ChessPieceView(Context context) {
        super(context);
        init();
    }

    public ChessPieceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChessPieceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Save initial touch position
                        downX = event.getX();
                        downY = event.getY();
                        originalPosition = new Point(getLeft(), getTop());
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Move the piece based on finger movement
                        float x = event.getRawX() - downX;
                        float y = event.getRawY() - downY;
                        setX(x);
                        setY(y);
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Snap to nearest cell after drag (simplified for now)
                        snapToNearestCell();
                        return true;
                }
                return false;
            }
        });
    }

    private void snapToNearestCell() {
        // Snap logic to ensure the piece aligns with the nearest grid cell
        int cellWidth = chessBoard.getWidth() / 8;
        int cellHeight = chessBoard.getHeight() / 8;

        int nearestX = (int) (getX() + (cellWidth / 2)) / cellWidth * cellWidth;
        int nearestY = (int) (getY() + (cellHeight / 2)) / cellHeight * cellHeight;

        // Apply snapping
        setX(nearestX);
        setY(nearestY);

        // Additional logic can be added to verify if the move is valid based on the piece type
    }

    public void setChessBoard(GridLayout chessBoard) {
        this.chessBoard = chessBoard;
    }
}
