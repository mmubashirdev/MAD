package com.example.contactbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.IntentCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/** Screen 1: contact list + "+" button. */
public class ContactListFragment extends Fragment {

    /** Implemented by the host activity, so this fragment never needs to know about MainActivity. */
    public interface OnContactSelectedListener {
        void onContactSelected(@NonNull Contact contact);
    }

    private final ContactRepository repository = ContactRepository.getInstance();
    private OnContactSelectedListener listener;
    private RecyclerView recyclerView;

    // Must be registered at construction time (before STARTED), which is why it is a field initialiser.
    private final ActivityResultLauncher<Intent> addContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onAddContactResult);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnContactSelectedListener) {
            listener = (OnContactSelectedListener) context;
        } else {
            throw new IllegalStateException(context + " must implement OnContactSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new ContactAdapter(repository.getAll(), contact -> {
            if (listener != null) listener.onContactSelected(contact);
        }));

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            // Explicit intent: the target is our own AddContactActivity, so we name the class instead of describing an action.
            addContactLauncher.launch(new Intent(requireContext(), AddContactActivity.class));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null; // avoid leaking the view hierarchy
    }

    private void onAddContactResult(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() != Activity.RESULT_OK || data == null) return; // Cancel / back: add nothing

        Contact contact = IntentCompat.getParcelableExtra(data, AddContactActivity.EXTRA_CONTACT, Contact.class);
        if (contact == null) return;

        int position = repository.add(contact);
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyItemInserted(position);
            recyclerView.scrollToPosition(position);
        }
        // If the view isn't alive right now, the adapter will read the updated repository when it's rebuilt.
    }
}
