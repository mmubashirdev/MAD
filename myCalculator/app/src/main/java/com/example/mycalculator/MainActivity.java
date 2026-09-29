package com.example.mycalculator; // Defines the namespace for this class

import android.os.Bundle; // Import for the Bundle class to save state (use for onCreate)
import android.widget.Button; // Import for the Button UI component from xml
import android.widget.EditText; // Import for the EditText UI component (input box)
import android.widget.TextView; // Import for the TextView UI component (text display)
import android.widget.Toast; // Import for the Toast class to show small pop-up messages

import androidx.activity.EdgeToEdge; // Import to enable edge-to-edge screen support
import androidx.appcompat.app.AppCompatActivity; // Base class for Activities that use modern Android features
import androidx.core.graphics.Insets; // Import to handle screen insets (safe areas)
import androidx.core.view.ViewCompat; // Import for backward-compatible view utilities
import androidx.core.view.WindowInsetsCompat; // Import to handle system window insets

public class MainActivity extends AppCompatActivity { // Main class defining the screen behavior

    private EditText et1, et2; // Declare variables for the two number input boxes
    private TextView tv2; // Declare variable for the result display text

    @Override // Indicates that this method overrides a method from the parent class
    protected void onCreate(Bundle savedInstanceState) { // Method called when the Activity is first created
        super.onCreate(savedInstanceState); // Call to the parent class's onCreate method
        EdgeToEdge.enable(this); // Enable edge-to-edge display for this Activity
        setContentView(R.layout.activity_main); // Link the Java code to the XML layout file

        // Listener to adjust view padding based on system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()); // Get system bar dimensions
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Apply padding to view
            return insets; // Return the insets to continue processing
        });

        et1 = findViewById(R.id.et1); // Initialize et1 by finding its ID in the XML layout
        et2 = findViewById(R.id.et2); // Initialize et2 by finding its ID in the XML layout
        tv2 = findViewById(R.id.tv2); // Initialize tv2 by finding its ID in the XML layout
        Button btn1 = findViewById(R.id.btn1); // Initialize btn1 by finding its ID in the XML layout

        btn1.setOnClickListener(v -> { // Set a listener to perform an action when the button is clicked
            String input1 = et1.getText().toString(); // Get the text from the first input box
            String input2 = et2.getText().toString(); // Get the text from the second input box

            if (input1.isEmpty() || input2.isEmpty()) { // Check if either input box is empty
                Toast.makeText(MainActivity.this, R.string.err_enter_values, Toast.LENGTH_SHORT).show(); // Show an error toast
                tv2.setText(R.string.err_enter_values); // Set the result text to the error message
                return; // Exit the listener if inputs are missing
            }

            try { // Use try-catch to handle potential number formatting errors
//                double num1 = Double.parseDouble(input1); // Convert the first input string to a double
//                double num2 = Double.parseDouble(input2); // Convert the second input string to a double
//                double sum = num1 + num2; // Perform the addition
                string sum = input1 + input2;
                tv2.setText(String.valueOf(sum)); // Display the sum result in the TextView
            } catch (NumberFormatException e) { // Catch block if the input is not a valid number
                Toast.makeText(MainActivity.this, R.string.err_invalid_input, Toast.LENGTH_SHORT).show(); // Show an invalid input toast
            }
        });
    }
}