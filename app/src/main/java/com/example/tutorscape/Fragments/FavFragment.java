package com.example.tutorscape.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Adapter.FavouriteAdapter;
import com.example.tutorscape.Adapter.TCAdapter;
import com.example.tutorscape.Model.Favourite;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<TuitionCentre> mTC;
    private FavouriteAdapter favouriteAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fav_fragment, container, false);
        Log.d("FavFragment", "Called");

        if(root == null) {
            Log.e("FavFragment", "Failed to inflate layout");
        }

        recyclerView = root.findViewById(R.id.recycler_view_fav);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mTC = new ArrayList<>();
        favouriteAdapter = new FavouriteAdapter(getContext(), mTC);
        recyclerView.setAdapter(favouriteAdapter);

        readFavourites();

        return root;
    }

    private void readFavourites() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();
        DatabaseReference tcRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("TuitionCentre");
        DatabaseReference favRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("Favourites/" + userId);

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mTC.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Favourite favourite = dataSnapshot.getValue(Favourite.class);
                    String TCID = favourite.getTCID();
                    tcRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            for(DataSnapshot dataSnapshot1 : snapshot1.getChildren()){
                                TuitionCentre tuitionCentre = dataSnapshot1.getValue(TuitionCentre.class);
                                if(TCID.equals(tuitionCentre.getId())){
                                    mTC.add(tuitionCentre);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                favouriteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
