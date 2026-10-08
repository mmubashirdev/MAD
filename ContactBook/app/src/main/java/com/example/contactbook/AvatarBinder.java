package com.example.contactbook;

import android.content.res.ColorStateList;
import android.widget.TextView;

import java.util.Locale;

/** Draws the coloured initial circle. Shared by the list row and the details header. */
final class AvatarBinder {

    private static final int[] PALETTE = {
            0xFF6750A4, 0xFFC62828, 0xFF2E7D32, 0xFF00695C, 0xFF1565C0, 0xFFEF6C00
    };

    private AvatarBinder() { }

    static void bind(TextView avatar, Contact contact) {
        avatar.setText(contact.getInitial());
        // Stable colour per name. "& 0x7fffffff" instead of Math.abs(): abs(Integer.MIN_VALUE) is still negative.
        int index = (contact.getName().toLowerCase(Locale.ROOT).hashCode() & 0x7fffffff) % PALETTE.length;
        avatar.setBackgroundTintList(ColorStateList.valueOf(PALETTE[index]));
    }
}
