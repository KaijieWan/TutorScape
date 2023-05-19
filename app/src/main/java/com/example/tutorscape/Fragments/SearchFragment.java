package com.example.tutorscape.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.example.tutorscape.Adapter.TCAdapter;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private AutoCompleteTextView search_bar;
    private List<TuitionCentre> mTC;
    private TCAdapter tcAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("SearchFragment", "onCreateView called");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        if(view == null) {
            Log.e("SearchFragment", "Failed to inflate layout");
        }

        recyclerView = view.findViewById(R.id.recycler_view_TC);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mTC = new ArrayList<>();
        tcAdapter = new TCAdapter(getContext(), mTC, true);
        recyclerView.setAdapter(tcAdapter);

        search_bar = view.findViewById(R.id.search_bar);
        return view;
    }

    private void readTC() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("TuitionCentre");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(TextUtils.isEmpty(search_bar.getText().toString())){
                    mTC.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        TuitionCentre tuitionCentre = snapshot.getValue(TuitionCentre.class);
                        mTC.add(tuitionCentre);
                    }
                    tcAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}