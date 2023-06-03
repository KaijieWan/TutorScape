package com.example.tutorscape.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Model.Review;
import com.example.tutorscape.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

public class ReviewAdapter extends  RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    private Context mContext;
    private List<Review> mReviews;

    public ReviewAdapter(Context mContext, List<Review> mReviews) {
        this.mContext = mContext;
        this.mReviews = mReviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.results_reviews_items, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Review review = mReviews.get(position);

        //implement the set text methods here
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users/" + review.getUID() + "/name");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                holder.user_name.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.review_date.setText(review.getReview_date());
        holder.subjects_enrolled.setText(review.getSubjects_enrolled());

        float rating_float = Float.parseFloat(review.getRating_num());
        holder.rating_bar.setRating(rating_float);

        holder.review_text.setText(review.getReview_text());
    }



    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView user_name;
        public TextView review_date;
        public TextView subjects_enrolled;
        public RatingBar rating_bar;
        public TextView review_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            review_date = itemView.findViewById(R.id.review_date);
            subjects_enrolled = itemView.findViewById(R.id.subjects_enrolled);
            rating_bar = itemView.findViewById(R.id.rating_bar);
            review_text = itemView.findViewById(R.id.user_review);
        }
    }
}
