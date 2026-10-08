package com.example.contactbook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;

/** Screen 2: details of the tapped contact + five action buttons. */
public class ContactDetailsFragment extends Fragment {

    private static final String ARG_CONTACT = "arg_contact";

    /** Factory method: the only way to build this fragment, so the required argument can't be forgotten. */
    public static ContactDetailsFragment newInstance(@NonNull Contact contact) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_CONTACT, contact);
        ContactDetailsFragment fragment = new ContactDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Contact contact = BundleCompat.getParcelable(requireArguments(), ARG_CONTACT, Contact.class);
        if (contact == null) throw new IllegalStateException("ContactDetailsFragment needs a Contact argument");

        AvatarBinder.bind(view.findViewById(R.id.tv_avatar), contact);
        ((TextView) view.findViewById(R.id.tv_name)).setText(contact.getName());
        setValue(view, R.id.tv_phone, contact.getPhone());
        setValue(view, R.id.tv_email, contact.getEmail());
        setValue(view, R.id.tv_website, contact.getWebsite());
        setValue(view, R.id.tv_address, contact.getAddress());

        view.findViewById(R.id.btn_call).setOnClickListener(v ->
                launch(ContactIntents.dial(contact.getPhone()), R.string.no_app_call));

        view.findViewById(R.id.btn_email).setOnClickListener(v -> {
            if (!contact.hasEmail()) { toast(R.string.no_email_saved); return; }
            String subject = getString(R.string.email_subject, contact.getName());
            launch(ContactIntents.email(contact.getEmail(), subject), R.string.no_app_email);
        });

        view.findViewById(R.id.btn_website).setOnClickListener(v -> {
            if (!contact.hasWebsite()) { toast(R.string.no_website_saved); return; }
            launch(ContactIntents.website(contact.getWebsite()), R.string.no_app_browser);
        });

        view.findViewById(R.id.btn_map).setOnClickListener(v -> {
            if (!contact.hasAddress()) { toast(R.string.no_address_saved); return; }
            launch(ContactIntents.map(contact.getAddress()), R.string.no_app_map);
        });

        view.findViewById(R.id.btn_share).setOnClickListener(v ->
                launch(ContactIntents.share(contact, getString(R.string.share_chooser_title)), R.string.no_app_share));
    }

    private void setValue(View root, int id, String value) {
        ((TextView) root.findViewById(id)).setText(value.isEmpty() ? getString(R.string.empty_value) : value);
    }

    /**
     * Single choke point for starting external apps. We catch ActivityNotFoundException instead of
     * calling resolveActivity(): since Android 11 resolveActivity() returns null unless the manifest
     * declares <queries>, so try/catch is both simpler and more reliable.
     */
    private void launch(Intent intent, @StringRes int errorMessage) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            toast(errorMessage);
        }
    }

    private void toast(@StringRes int message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
