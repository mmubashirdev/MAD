package com.example.contactbook;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * Pure factory for the five implicit intents. No Context/UI here, so it is trivially unit-testable,
 * and the fragment only has to worry about launching.
 */
final class ContactIntents {

    private ContactIntents() { }

    static Intent dial(@NonNull String phone) {
        // ACTION_DIAL (not ACTION_CALL): opens the dialer with the number pre-filled, needs no CALL_PHONE permission, and the user stays in control.
        String digits = phone.replaceAll("[\\s()\\-]", "");
        return new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", digits, null));
    }

    static Intent email(@NonNull String address, @NonNull String subject) {
        // ACTION_SENDTO + "mailto:" data limits the chooser to real email apps (plain ACTION_SEND would also list Bluetooth, chat apps, etc.).
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        return intent;
    }

    static Intent website(@NonNull String website) {
        // ACTION_VIEW with an http(s) URI is the standard way to ask for a browser; any other scheme is forced to https so file:// or custom schemes can't sneak in.
        String lower = website.toLowerCase(Locale.ROOT);
        String url = (lower.startsWith("http://") || lower.startsWith("https://")) ? website : "https://" + website;
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    static Intent map(@NonNull String address) {
        // ACTION_VIEW with a geo: URI is understood by every map app; "0,0?q=" tells it to geocode the address text instead of using coordinates.
        return new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(address)));
    }

    static Intent share(@NonNull Contact contact, @NonNull String chooserTitle) {
        StringBuilder text = new StringBuilder();
        text.append("Name: ").append(contact.getName()).append('\n');
        text.append("Phone: ").append(contact.getPhone());
        if (contact.hasEmail()) text.append('\n').append("Email: ").append(contact.getEmail());

        // ACTION_SEND with text/plain lets the user pick ANY app that accepts text; createChooser forces the picker every time instead of a remembered default.
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, text.toString());
        return Intent.createChooser(send, chooserTitle);
    }
}
