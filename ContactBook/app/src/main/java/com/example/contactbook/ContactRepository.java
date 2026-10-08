package com.example.contactbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** In-memory data source (no database required). Single place that owns the contact list. */
public final class ContactRepository {

    private static final ContactRepository INSTANCE = new ContactRepository();

    private final List<Contact> contacts = new ArrayList<>();

    private ContactRepository() {
        contacts.add(new Contact("Sara Ahmed", "0300 1234567", "sara@example.com", "https://example.com", "Lahore, Pakistan"));
        contacts.add(new Contact("Usman Ali", "0301 7654321", "usman@example.com", "https://example.org", "Karachi, Pakistan"));
        contacts.add(new Contact("Hina Malik", "0322 1112233", "hina@example.com", "https://example.net", "Islamabad, Pakistan"));
        contacts.add(new Contact("Bilal Raza", "0333 4445566", "bilal@example.com", "https://example.com", "Faisalabad, Pakistan"));
    }

    public static ContactRepository getInstance() { return INSTANCE; }

    /** Read-only LIVE view: callers can't mutate it, but they see additions. */
    public List<Contact> getAll() { return Collections.unmodifiableList(contacts); }

    /** @return index of the newly added contact (handy for notifyItemInserted). */
    public int add(Contact contact) {
        contacts.add(contact);
        return contacts.size() - 1;
    }
}
