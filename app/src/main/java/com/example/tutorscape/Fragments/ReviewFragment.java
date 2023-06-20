package com.example.tutorscape.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Adapter.TCAdapter;
import com.example.tutorscape.Adapter.YourReviewsAdapter;
import com.example.tutorscape.Model.Review;
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

public class ReviewFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Review> mReviews;
    private YourReviewsAdapter yourReviewsAdapter;
    private FirebaseAuth firebaseAuth;
    private String user_name;
    private String userId;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.review_fragment, container, false);
        Log.d("ReviewFragment", "Called");
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("Users/" + userId + "/name");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_name = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView = root.findViewById(R.id.recycler_view_user_reviews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mReviews = new ArrayList<>();
        yourReviewsAdapter = new YourReviewsAdapter(getContext(), mReviews, user_name);
        recyclerView.setAdapter(yourReviewsAdapter);
        Log.d("ReviewFragment", "second call");

        readReviews();

        return root;
    }

    private void readReviews() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Reviews");
        Log.d("readReviews", "called");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("onDataChange", "called");
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Review review = dataSnapshot.getValue(Review.class);
                    if(review.getUID().equals(userId)){
                        mReviews.add(review);
                    }
                }
                Log.d("readReviews", mReviews.toString());
                yourReviewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReviewFragment", "Database error: " + error.getMessage());
            }
        });
    }
}
