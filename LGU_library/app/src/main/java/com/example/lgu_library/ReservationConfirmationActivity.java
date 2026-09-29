package com.example.lgu_library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReservationConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_confirmation);

        TextView reservationDetailsText = findViewById(R.id.reservationDetailsText);

        Intent intent = getIntent();
        String bookTitle = (intent != null && intent.hasExtra("BOOK_TITLE")) ? intent.getStringExtra("BOOK_TITLE") : "";
        String bookAuthor = (intent != null && intent.hasExtra("BOOK_AUTHOR")) ? intent.getStringExtra("BOOK_AUTHOR") : "";
        String studentName = (intent != null && intent.hasExtra("STUDENT_NAME")) ? intent.getStringExtra("STUDENT_NAME") : "";

        String details =
                "Book: " + bookTitle +
                        "\nAuthor: " + bookAuthor +
                        "\nReserved : " + studentName;

        reservationDetailsText.setText(details);
    }
}