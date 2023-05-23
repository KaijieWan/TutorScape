package com.example.tutorscape.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import android.widget.ImageView;

import com.example.tutorscape.Adapter.TCAdapter;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

        readTC();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    readTC();
                } else {
                    searchTC(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void readTC() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("TuitionCentre");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SearchFragment", "onDataChange called");
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
                Log.e("SearchFragment", "Database error: " + error.getMessage());
            }
        });
        Log.d("SearchFragment", "Database Reference: " + ref.toString());
    }

    private void searchTC (String s) {
        mTC.clear();
        //Set<TuitionCentre> tuitionCentres = new HashSet<>();
        Set<String> uniqueIds = new HashSet<>();

        List<Query> queries = new ArrayList<>();

        String startValue = s.substring(0, 1).toUpperCase() + s.substring(1);
        String endValue = startValue + "\uf8ff";

        Query query_name = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("TuitionCentre")
                .orderByChild("name").startAt(startValue).endAt(endValue);
        queries.add(query_name);

        Query query_address = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("TuitionCentre")
                .orderByChild("address").startAt(startValue).endAt(endValue);
        queries.add(query_address);

        final int expectedQueryCount = queries.size();
        final AtomicInteger completedQueryCount = new AtomicInteger(0);

        for(Query query : queries){
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String TCID = snapshot.getKey();
                        if(!uniqueIds.contains(TCID)){
                            uniqueIds.add(TCID);
                            TuitionCentre tuitionCentre = snapshot.getValue(TuitionCentre.class);
                            mTC.add(tuitionCentre);
                            //tuitionCentres.add(tuitionCentre);
                        }
                    }
                    if(completedQueryCount.incrementAndGet() == expectedQueryCount){
                        //mTC.addAll(tuitionCentres);
                        tcAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

}