package com.example.tutorscape.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Adapter.TCAdapter;
import com.example.tutorscape.Adapter.YourReviewsAdapter;
import com.example.tutorscape.Model.Review;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.Model.Updates;
import com.example.tutorscape.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReviewFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Review> mReviews;
    private YourReviewsAdapter yourReviewsAdapter;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private ImageView filterIcon;
    private FrameLayout filterAnchor;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.review_fragment, container, false);
        Log.d("ReviewFragment", "Called");
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getUid();

        recyclerView = root.findViewById(R.id.recycler_view_user_reviews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mReviews = new ArrayList<>();
        yourReviewsAdapter = new YourReviewsAdapter(getContext(), mReviews, userId);
        recyclerView.setAdapter(yourReviewsAdapter);

        Log.d("ReviewFragment", "second call");

        readReviews();

        filterIcon = root.findViewById(R.id.reviews_filter_icon);
        filterAnchor = root.findViewById(R.id.filter_anchor);
        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(requireContext(), filterAnchor);
                popupMenu.getMenuInflater().inflate(R.menu.update_filter_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemID = item.getItemId();
                        if(itemID == R.id.option_newest_to_oldest) {
                            applyDateSorting(true);
                            return true;
                        }
                        else if(itemID == R.id.option_oldest_to_newest) {
                            applyDateSorting(false);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

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

    private void applyDateSorting(boolean ascending) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        Collections.sort(mReviews, new Comparator<Review>() {
            @Override
            public int compare(Review o1, Review o2) {
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = dateFormat.parse(o1.getReview_date());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                try {
                    date2 = dateFormat.parse(o2.getReview_date());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (ascending) {
                    return date1.compareTo(date2);
                } else {
                    return date2.compareTo(date1);
                }
            }
        });
        // Notify the adapter that the data has changed
        // Assuming you have an adapter for the RecyclerView
        yourReviewsAdapter.notifyDataSetChanged();
    }
}
