package com.example.contactbook;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Locale;

/** Immutable contact. Parcelable so it can travel in a Bundle (fragment args) or an Intent (activity result). */
public final class Contact implements Parcelable {

    private final String name;
    private final String phone;
    private final String email;
    private final String website;
    private final String address;

    public Contact(String name, String phone, String email, String website, String address) {
        this.name = clean(name);
        this.phone = clean(phone);
        this.email = clean(email);
        this.website = clean(website);
        this.address = clean(address);
    }

    private static String clean(String s) {
        return s == null ? "" : s.trim();
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getWebsite() { return website; }
    public String getAddress() { return address; }

    public boolean hasEmail() { return !email.isEmpty(); }
    public boolean hasWebsite() { return !website.isEmpty(); }
    public boolean hasAddress() { return !address.isEmpty(); }

    /** First character of the name, code-point safe (emoji / non-BMP names won't be cut in half). */
    public String getInitial() {
        if (name.isEmpty()) return "?";
        String first = new String(Character.toChars(name.codePointAt(0)));
        return first.toUpperCase(Locale.getDefault());
    }

    // ---- Parcelable boilerplate ----

    private Contact(Parcel in) {
        this(in.readString(), in.readString(), in.readString(), in.readString(), in.readString());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(website);
        dest.writeString(address);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override public Contact createFromParcel(Parcel in) { return new Contact(in); }
        @Override public Contact[] newArray(int size) { return new Contact[size]; }
    };
}
