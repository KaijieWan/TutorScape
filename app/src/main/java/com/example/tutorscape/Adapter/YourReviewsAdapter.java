package com.example.tutorscape.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Model.Review;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class YourReviewsAdapter extends RecyclerView.Adapter<YourReviewsAdapter.ViewHolder> {
    private Context mContext;
    private List<Review> reviewList;
    private String userId;

    public YourReviewsAdapter(Context mContext, List<Review> reviewList, String userId) {
        this.mContext = mContext;
        this.reviewList = reviewList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.your_reviews_items, parent, false);
        return new YourReviewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YourReviewsAdapter.ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        DatabaseReference nameRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users/" + userId + "/name");

        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user_name = snapshot.getValue(String.class);
                holder.user_name.setText(user_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //implement the rest of the set text methods here
        holder.review_date.setText(review.getReview_date());
        holder.subjects_enrolled.setText(review.getSubjects_enrolled());

        float rating_float = Float.parseFloat(review.getRating_num());
        holder.rating_bar.setRating(rating_float);

        holder.review_text.setText(review.getReview_text());

        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("TuitionCentre");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    TuitionCentre tuitionCentre = dataSnapshot.getValue(TuitionCentre.class);
                    if(tuitionCentre.getId().equals(review.getTCID())){
                        holder.tc_name.setText(capitalizeAfterSpace(tuitionCentre.getName()));
                        holder.tc_address.setText(capitalizeAfterSpace(tuitionCentre.getAddress()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView user_name;
        public TextView review_date;
        public TextView subjects_enrolled;
        public RatingBar rating_bar;
        public TextView review_text;
        public TextView tc_name;
        public TextView tc_address;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            review_date = itemView.findViewById(R.id.review_date);
            subjects_enrolled = itemView.findViewById(R.id.subjects_enrolled);
            rating_bar = itemView.findViewById(R.id.rating_bar);
            review_text = itemView.findViewById(R.id.user_review);
            tc_name = itemView.findViewById(R.id.tc_name);
            tc_name.setPaintFlags(tc_name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            tc_address = itemView.findViewById(R.id.tc_address);
        }
    }

    public String capitalizeAfterSpace(String input) {
        StringBuilder output = new StringBuilder();

        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                output.append(c);
            } else {
                if (capitalizeNext) {
                    c = Character.toUpperCase(c);
                    capitalizeNext = false;
                }
                output.append(c);
            }
        }
        return output.toString();
    }
}
