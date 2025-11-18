package com.example.safetyalert;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ContactAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Contact> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        recyclerView = findViewById(R.id.recycler1);
        progressBar = findViewById(R.id.progress_bar);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String userphonenumber = sh.getString("phone", "");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();

        loadContacts(userphonenumber);
    }

    private void loadContacts(String userphonenumber) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("users").document(userphonenumber).collection("contacts").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Contact contact = document.toObject(Contact.class);
                            contactList.add(contact);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ContactList.this, "No contacts found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ContactList.this, "Failed to load contacts", Toast.LENGTH_SHORT).show();
                });
    }
}
