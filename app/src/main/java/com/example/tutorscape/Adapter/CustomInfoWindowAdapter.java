package com.example.tutorscape.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.tutorscape.Model.Review;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.R;
import com.example.tutorscape.ResultsActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater;
    private View infoWindowView;
    private TuitionCentre currentTuitionCentre;
    private Context mContext;
    //private List<Review> reviewsList = new ArrayList<>();
    private List<Review> currentReviewsList;

    public CustomInfoWindowAdapter(LayoutInflater inflater, Context mContext){
        this.inflater = inflater;
        //this.tuitionCentre = tuitionCentre;
        this.mContext = mContext;
    }
    public interface OnViewsUpdatedListener{
        void onViewsUpdated();
    }
    public interface InfoWindowCallback{
        void onInfoWindowDataReady(View infoWindowView, List<Review> reviewsList);
    }
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }


    private CompletableFuture<List<Review>> fetchReviews(DatabaseReference ref, String tuitionCentreId) {
        CompletableFuture<List<Review>> future = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Review> reviewsList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    if (review.getTCID().equals(tuitionCentreId)) {
                        reviewsList.add(review);
                    }
                }

                future.complete(reviewsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }

    private void updateViews(View infoWindowView, TuitionCentre tuitionCentre, List<Review> reviewsList /*OnViewsUpdatedListener listener*/){
        Log.d("reviewsList", reviewsList.toString());

        //infoWindowView.setBackgroundResource(R.color.cyan);

        ImageView tuitionImage = infoWindowView.findViewById(R.id.image_name);

        TextView tuitionName = infoWindowView.findViewById(R.id.tuition_name);
        tuitionName.setPaintFlags(tuitionName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        TextView tuitionAddr = infoWindowView.findViewById(R.id.tuition_address);
        TextView tuitionPostal = infoWindowView.findViewById(R.id.tuition_postal);
        TextView tuitionContactNo = infoWindowView.findViewById(R.id.tuition_contact);
        TextView tuitionWebsite = infoWindowView.findViewById(R.id.tuition_website);
        TextView tuitionRatingNum = infoWindowView.findViewById(R.id.rating_num);
        RatingBar tuitionRatingBar = infoWindowView.findViewById(R.id.rating_bar);

        String address_msg = mContext.getString(R.string.address_msg, capitalizeAfterSpace(tuitionCentre.getAddress()));
        String postal_msg = mContext.getString(R.string.postal_msg, tuitionCentre.getPostal());
        String contact_msg = mContext.getString(R.string.contact_msg, tuitionCentre.getContactNo());
        String website_msg = mContext.getString(R.string.website_msg, tuitionCentre.getWebsite());

        SpannableString address_span = new SpannableString(address_msg);
        address_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString postal_span = new SpannableString(postal_msg);
        postal_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString contact_span = new SpannableString(contact_msg);
        contact_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString website_span = new SpannableString(website_msg);
        website_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tuitionName.setText(capitalizeAfterSpace(tuitionCentre.getName()));
        tuitionAddr.setText(address_span);
        tuitionPostal.setText(postal_span);
        tuitionContactNo.setText(contact_span);
        tuitionWebsite.setText(website_span);

        //reviewsList = new ArrayList<>();
        //Review views
        /*String tuitionCentreId = tuitionCentre.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Reviews");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange - CustomInfoWindowAdapter", "called");
                //reviewsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Review review = snapshot.getValue(Review.class);
                    if(review.getTCID().equals(tuitionCentreId)){
                        reviewsList.add(review);
                    }
                }*/
                if(!reviewsList.isEmpty()){
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
                    tuitionRatingBar.setRating(Float.parseFloat(decimalFormat.format(rating_float)));

                    Log.d("onDataChange - CustomInfoWindowAdapter", decimalFormat.format(rating_float));

                    String rating_msg = mContext.getString(R.string.rating_msg, decimalFormat.format(rating_float));
                    SpannableString rating_span = new SpannableString(rating_msg);
                    rating_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tuitionRatingNum.setText(rating_span);
                }
                else{
                    tuitionRatingBar.setRating(0.0f);
                    tuitionRatingNum.setText("No ratings");
                }
            //}

            /*@Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        Picasso.get().load(tuitionCentre.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(tuitionImage);
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        TuitionCentre tuitionCentre = (TuitionCentre) marker.getTag();

        if(infoWindowView == null){
            infoWindowView = inflater.inflate(R.layout.tuition_centre_item, null);
            infoWindowView.setBackgroundColor(0xFF04FFFF);
        }

        if (tuitionCentre.equals(currentTuitionCentre)) {
            // Same marker clicked again, return the already prepared info window
            updateViews(infoWindowView, currentTuitionCentre, currentReviewsList);
            return infoWindowView;
        } else {
            // Different marker clicked, fetch new data and prepare info window
            currentTuitionCentre = tuitionCentre;
            String tuitionCentreId = currentTuitionCentre.getId();
            DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference().child("Reviews");

            CompletableFuture<List<Review>> fetchReviewsFuture = fetchReviews(ref, tuitionCentreId);
            fetchReviewsFuture.thenAccept(reviewsList -> {
                currentReviewsList = reviewsList;
                updateViews(infoWindowView, currentTuitionCentre, currentReviewsList);
                marker.showInfoWindow(); // Show the updated info window after data is ready
            });

            return null; // Return null for now, the info window will be updated asynchronously
        }

        //tuitionCentreId = tuitionCentreId;

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
