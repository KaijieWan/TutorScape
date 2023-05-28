package com.example.tutorscape.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.example.tutorscape.Adapter.UpdatesAdapter;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.Model.Updates;
import com.example.tutorscape.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdatesFragment extends Fragment {

    private RecyclerView recyclerViewUpdates;
    private UpdatesAdapter updatesAdapter;
    private  List<Updates> updatesList;
    private AutoCompleteTextView search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_updates, container, false);

        recyclerViewUpdates = view.findViewById(R.id.recycler_view_updates);
        recyclerViewUpdates.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewUpdates.setLayoutManager(linearLayoutManager);
        updatesList = new ArrayList<>();
        updatesAdapter = new UpdatesAdapter(getContext(), updatesList);
        recyclerViewUpdates.setAdapter(updatesAdapter);

        search_bar = view.findViewById(R.id.update_search_bar);
        readUpdates();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    readUpdates();
                } else {
                    searchUpdates(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void searchUpdates(String s) {
        updatesList.clear();
        Set<String> uniqueIds = new HashSet<>();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Updates");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uID = snapshot.getKey();
                    if (!uniqueIds.contains(uID)) {
                        uniqueIds.add(uID);
                        Updates update = snapshot.getValue(Updates.class);

                        // Perform substring search on relevant attributes
                        if (substringSearch(update.getUpdateName(), s)) {
                            updatesList.add(update);
                        }
                    }
                }
                // Update the adapter with the filtered data
                updatesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void readUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Updates");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SearchFragment", "onDataChange called");
                if(TextUtils.isEmpty(search_bar.getText().toString())){
                    updatesList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Updates update = snapshot.getValue(Updates.class);
                        updatesList.add(update);
                    }
                    updatesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchFragment", "Database error: " + error.getMessage());
            }
        });
        Log.d("SearchFragment", "Database Reference: " + ref.toString());
    }

    private boolean substringSearch(String attribute, String substring) {
        return attribute.toLowerCase().contains(substring.toLowerCase());
    }

}