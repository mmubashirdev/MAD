package com.example.lgu_library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookDetailsActivity extends AppCompatActivity {

    private TextView bookTitleText;
    private TextView bookAuthorText;
    private EditText studentNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        bookTitleText = findViewById(R.id.bookTitleText);
        bookAuthorText = findViewById(R.id.bookAuthorText);
        studentNameInput = findViewById(R.id.studentNameInput);
        Button reserveButton = findViewById(R.id.reserveButton);

        reserveButton.setOnClickListener(v -> {

            String studentName = studentNameInput.getText().toString();

            if (studentName.trim().isEmpty()) {
                studentNameInput.setError("Please enter your name");
                studentNameInput.requestFocus();
                return;
            }

            String bookTitle = bookTitleText.getText().toString().replace("Title: ", "").trim();
            String bookAuthor = bookAuthorText.getText().toString().replace("Author: ", "").trim();

            Intent intent = new Intent(
                    BookDetailsActivity.this,
                    ReservationConfirmationActivity.class
            );

            intent.putExtra("BOOK_TITLE", bookTitle);
            intent.putExtra("BOOK_AUTHOR", bookAuthor);
            intent.putExtra("STUDENT_NAME", studentName);

            startActivity(intent);
        });
    }
}