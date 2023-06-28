package com.example.tutorscape.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Model.Favourite;
import com.example.tutorscape.Model.Review;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.R;
import com.example.tutorscape.ResultsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder>{
    private Context mContext;
    private List<TuitionCentre> mTC;
    private List<Review> reviewsList;
    private List<Favourite> favouriteList;

    public FavouriteAdapter(Context mContext, List<TuitionCentre> mTC) {
        this.mContext = mContext;
        this.mTC = mTC;
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fav_tuition_centre_item, parent, false);
        return new FavouriteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.ViewHolder holder, int position) {
        TuitionCentre TC = mTC.get(position);

        String address_msg = mContext.getString(R.string.address_msg, capitalizeAfterSpace(TC.getAddress()));
        String postal_msg = mContext.getString(R.string.postal_msg, TC.getPostal());
        String contact_msg = mContext.getString(R.string.contact_msg, TC.getContactNo());
        String website_msg = mContext.getString(R.string.website_msg, TC.getWebsite());

        SpannableString address_span = new SpannableString(address_msg);
        address_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString postal_span = new SpannableString(postal_msg);
        postal_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString contact_span = new SpannableString(contact_msg);
        contact_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString website_span = new SpannableString(website_msg);
        website_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tuitionName.setText(capitalizeAfterSpace(TC.getName()));
        holder.tuitionAddr.setText(address_span);
        holder.tuitionPostal.setText(postal_span);
        holder.tuitionContactNo.setText(contact_span);
        holder.tuitionWebsite.setText(website_span);

        reviewsList = new ArrayList<>();
        //Review views
        String tuitionCentreId = TC.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Reviews");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Review review = snapshot.getValue(Review.class);
                    if(review.getTCID().equals(tuitionCentreId)){
                        reviewsList.add(review);
                    }
                }
                int totalReviews = reviewsList.size();
                int rating5 = 0, rating4 = 0, rating3 = 0, rating2 = 0, rating1 = 0;
                for(Review review : reviewsList){
                    switch (review.getRating_num()) {
                        case "1" -> rating1++;
                        case "2" -> rating2++;
                        case "3" -> rating3++;
                        case "4" -> rating4++;
                        case "5" -> rating5++;
                        default -> {
                        }
                    }
                }
                int totalWeighted_rating = (rating5 * 5) + (rating4 * 4) + (rating3 * 3) + (rating2 * 2) + rating1;
                float rating_float = (float) totalWeighted_rating / totalReviews;
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                holder.tuitionRatingBar.setRating(Float.parseFloat(decimalFormat.format(rating_float)));

                String rating_msg = mContext.getString(R.string.rating_msg, decimalFormat.format(rating_float));
                SpannableString rating_span = new SpannableString(rating_msg);
                rating_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tuitionRatingNum.setText(rating_span);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Picasso.get().load(TC.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.tuitionImage);


        // Bind data to the item view

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the item click event
                Intent intent = new Intent(mContext, ResultsActivity.class);
                intent.putExtra("tuitionCentreId", TC.getId());
                // Redirect to another page or view based on the selected item
                mContext.startActivity(intent);
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();
        DatabaseReference favRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("Favourites/" + userId);
        holder.favourite_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Favourite favourite = dataSnapshot.getValue(Favourite.class);
                            if(favourite.getTCID().equals(tuitionCentreId)){
                                favRef.child(tuitionCentreId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(mContext, "Removed from Favourites!", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Log.d("ResultsActivity", "removeValue of favourites failed: " + task.getException().getMessage());
                                            Toast.makeText(mContext, "Removing unsuccessful!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTC.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView tuitionImage;
        public TextView tuitionName;
        public TextView tuitionAddr;
        public TextView tuitionPostal;
        public TextView tuitionContactNo;
        public TextView tuitionWebsite;
        public TextView tuitionRatingNum;
        public RatingBar tuitionRatingBar;
        public ImageView favourite_click;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tuitionImage = itemView.findViewById(R.id.image_name);

            tuitionName = itemView.findViewById(R.id.tuition_name);
            tuitionName.setPaintFlags(tuitionName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            tuitionAddr = itemView.findViewById(R.id.tuition_address);
            tuitionPostal = itemView.findViewById(R.id.tuition_postal);
            tuitionContactNo = itemView.findViewById(R.id.tuition_contact);
            tuitionWebsite = itemView.findViewById(R.id.tuition_website);
            tuitionRatingNum = itemView.findViewById(R.id.rating_num);
            tuitionRatingBar = itemView.findViewById(R.id.rating_bar);
            favourite_click = itemView.findViewById(R.id.fav_click);
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
