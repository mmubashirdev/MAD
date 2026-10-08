package com.example.contactbook;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;

/** Hosts Screen 1 and Screen 2 and swaps them with FragmentManager (no Navigation Component). */
public class MainActivity extends AppCompatActivity implements ContactListFragment.OnContactSelectedListener {

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);

        applyInsets();

        toolbar = findViewById(R.id.toolbar);
        // Toolbar arrow behaves exactly like the system back button.
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Only add the first fragment on a fresh start; after rotation FragmentManager restores it by itself.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, new ContactListFragment())
                    .commit();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateToolbar);
        updateToolbar();
    }

    @Override
    public void onContactSelected(@NonNull Contact contact) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, ContactDetailsFragment.newInstance(contact))
                .addToBackStack(null) // gives us system-back for free
                .commit();
    }

    /** Single source of truth for the toolbar: derived from the back stack, never set by hand elsewhere. */
    private void updateToolbar() {
        FragmentManager fm = getSupportFragmentManager();
        boolean showingDetails = fm.getBackStackEntryCount() > 0;
        toolbar.setTitle(showingDetails ? R.string.title_contact_details : R.string.title_contact_book);
        if (showingDetails) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        } else {
            toolbar.setNavigationIcon(null);
        }
    }

    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (root, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            root.setPadding(bars.left, bars.top, bars.right, 0);
            findViewById(R.id.fragment_container).setPadding(0, 0, 0, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
