package com.example.contactbook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/** Screen 3: the form. Returns the new Contact to the caller via setResult(); never touches the list itself. */
public class AddContactActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT = "com.example.contactbook.EXTRA_CONTACT";

    private TextInputLayout nameLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout emailLayout;
    private TextInputEditText nameInput;
    private TextInputEditText phoneInput;
    private TextInputEditText emailInput;
    private TextInputEditText websiteInput;
    private TextInputEditText addressInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_contact);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);

        View content = findViewById(R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (root, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            root.setPadding(bars.left, bars.top, bars.right, 0);
            content.setPadding(0, 0, 0, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> cancel());

        nameLayout = findViewById(R.id.til_name);
        phoneLayout = findViewById(R.id.til_phone);
        emailLayout = findViewById(R.id.til_email);
        nameInput = findViewById(R.id.et_name);
        phoneInput = findViewById(R.id.et_phone);
        emailInput = findViewById(R.id.et_email);
        websiteInput = findViewById(R.id.et_website);
        addressInput = findViewById(R.id.et_address);

        findViewById(R.id.btn_save).setOnClickListener(v -> save());
        findViewById(R.id.btn_cancel).setOnClickListener(v -> cancel());
    }

    private void save() {
        String name = text(nameInput);
        String phone = text(phoneInput);
        String email = text(emailInput);

        nameLayout.setError(name.isEmpty() ? getString(R.string.error_name_required) : null);
        phoneLayout.setError(phone.isEmpty() ? getString(R.string.error_phone_required) : null);
        // Email is optional, but if the user typed something it should at least look like an address.
        boolean emailBad = !email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches();
        emailLayout.setError(emailBad ? getString(R.string.error_email_invalid) : null);

        if (name.isEmpty()) { nameInput.requestFocus(); return; }
        if (phone.isEmpty()) { phoneInput.requestFocus(); return; }
        if (emailBad) { emailInput.requestFocus(); return; }

        Contact contact = new Contact(name, phone, email, text(websiteInput), text(addressInput));
        Intent result = new Intent().putExtra(EXTRA_CONTACT, contact);
        setResult(RESULT_OK, result);
        finish();
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private static String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
