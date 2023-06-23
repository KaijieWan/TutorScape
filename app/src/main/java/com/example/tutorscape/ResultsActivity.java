package com.example.tutorscape;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tutorscape.Adapter.ReviewAdapter;
import com.example.tutorscape.Model.Review;
import com.example.tutorscape.Model.TuitionCentre;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewsList;
    private Context context;
    private String tuitionCentreId;
    private TuitionCentre TC;
    private ImageView tuitionImage;
    private TextView tuitionName;
    private TextView tuitionAddr;
    private TextView tuitionPostal;
    private TextView tuitionContactNo;
    private TextView tuitionWebsite;
    private TextView tuitionLevels;
    private TextView tuitionSubjects;
    private TextView tuitionExams;
    private TextView tuitionType;
    private TextView tuitionOperatingHrs;

    private TextView tuitionRatingNum;
    private RatingBar tuitionRatingBar;
    private ProgressBar progressBar5;
    private TextView percentage5;
    private ProgressBar progressBar4;
    private TextView percentage4;
    private ProgressBar progressBar3;
    private TextView percentage3;
    private ProgressBar progressBar2;
    private TextView percentage2;
    private ProgressBar progressBar1;
    private TextView percentage1;
    private TextView totalNoReviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //Receive bundle info
        context = this;
        Bundle intent = getIntent().getExtras();
        if(intent != null){
            tuitionCentreId = intent.getString("tuitionCentreId");
        }


        recyclerViewReviews = findViewById(R.id.recycler_view_TCReviews);
        recyclerViewReviews.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewReviews.setLayoutManager(linearLayoutManager);
        reviewsList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(context, reviewsList);
        recyclerViewReviews.setAdapter(reviewAdapter);

        filterReviews();

        tuitionImage = findViewById(R.id.image_name);

        tuitionName = findViewById(R.id.tuition_name);
        tuitionName.setPaintFlags(tuitionName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tuitionAddr = findViewById(R.id.tuition_address);
        tuitionPostal = findViewById(R.id.tuition_postal);
        tuitionContactNo = findViewById(R.id.tuition_contact);
        tuitionWebsite = findViewById(R.id.tuition_website);
        tuitionLevels = findViewById(R.id.tuition_levels);
        tuitionSubjects = findViewById(R.id.tuition_subjects);
        tuitionExams = findViewById(R.id.tuition_exams);
        tuitionType = findViewById(R.id.tuition_type);
        tuitionOperatingHrs = findViewById(R.id.tuition_operatingHrs);

        tuitionRatingNum = findViewById(R.id.rating_num);
        tuitionRatingBar = findViewById(R.id.rating_bar);
        progressBar5 = findViewById(R.id.ratingBar5);
        percentage5 = findViewById(R.id.five_star_percentage);
        progressBar4 = findViewById(R.id.ratingBar4);
        percentage4 = findViewById(R.id.four_star_percentage);
        progressBar3 = findViewById(R.id.ratingBar3);
        percentage3 = findViewById(R.id.three_star_percentage);
        progressBar2 = findViewById(R.id.ratingBar2);
        percentage2 = findViewById(R.id.two_star_percentage);
        progressBar1 = findViewById(R.id.ratingBar1);
        percentage1 = findViewById(R.id.one_star_percentage);

        totalNoReviews = findViewById(R.id.totalNo_reviews);

        //Implement the data retrieval for rest of the views and set the views with the data
        DatabaseReference TCref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("TuitionCentre").child(tuitionCentreId);

        TCref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TuitionCentre tuitionCentre = snapshot.getValue(TuitionCentre.class);
                    Picasso.get().load(tuitionCentre.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(tuitionImage);

                    String address_msg = getString(R.string.address_msg, capitalizeAfterSpace(tuitionCentre.getAddress()));
                    String postal_msg = getString(R.string.postal_msg, tuitionCentre.getPostal());
                    String contact_msg = getString(R.string.contact_msg, tuitionCentre.getContactNo());
                    String website_msg = getString(R.string.website_msg, tuitionCentre.getWebsite());
                    String levels_msg = getString(R.string.levels_msg, extractAndStitch(tuitionCentre.getLevels()));
                    String subjects_msg = getString(R.string.subjects_msg, extractAndStitch_subjects(tuitionCentre.getSubjects()));
                    String exams_msg = getString(R.string.exams_msg, extractAndStitch(tuitionCentre.getExams()));
                    String type_msg = getString(R.string.type_msg, tuitionCentre.getType());
                    String opHrs_msg = getString(R.string.opHrs_msg, tuitionCentre.getOperatingHrs());

                    SpannableString address_span = new SpannableString(address_msg);
                    address_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableString postal_span = new SpannableString(postal_msg);
                    postal_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableString contact_span = new SpannableString(contact_msg);
                    contact_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableString website_span = new SpannableString(website_msg);
                    website_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableString levels_span = new SpannableString(levels_msg);
                    levels_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableString subjects_span = new SpannableString(subjects_msg);
                    subjects_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableString exams_span = new SpannableString(exams_msg);
                    exams_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableString type_span = new SpannableString(type_msg);
                    type_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableString opHrs_span = new SpannableString(opHrs_msg);
                    opHrs_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tuitionName.setText(capitalizeAfterSpace(tuitionCentre.getName()));
                    tuitionAddr.setText(address_span);
                    tuitionPostal.setText(postal_span);
                    tuitionContactNo.setText(contact_span);
                    tuitionWebsite.setText(website_span);
                    tuitionLevels.setText(levels_span);
                    tuitionSubjects.setText(subjects_span);
                    tuitionExams.setText(exams_span);
                    tuitionType.setText(type_span);
                    tuitionOperatingHrs.setText(opHrs_span);

                    //Setting of views related to reviews portion

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterReviews() {
        Log.d("filterReviews", "called");
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("Reviews");
        Log.d("filterReviews", ref.toString());
        Log.d("filterReviews", tuitionCentreId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("filterReviews", tuitionCentreId);
                reviewsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Review review = snapshot.getValue(Review.class);
                    if(review.getTCID().equals(tuitionCentreId)){
                        reviewsList.add(review);
                    }
                }
                Log.d("Reviews", reviewsList.toString());
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

                int progress5 = 0;
                int progress4 = 0;
                int progress3 = 0;
                int progress2 = 0;
                int progress1 = 0;

                if(totalReviews != 0){
                    progress5 = (rating5 * 100) / totalReviews;
                    progress4 = (rating4 * 100) / totalReviews;
                    progress3 = (rating3 * 100) / totalReviews;
                    progress2 = (rating2 * 100) / totalReviews;
                    progress1 = (rating1 * 100) / totalReviews;
                }


                percentage5.setText(getString(R.string.percentage_placeholder, progress5));
                percentage4.setText(getString(R.string.percentage_placeholder, progress4));
                percentage3.setText(getString(R.string.percentage_placeholder, progress3));
                percentage2.setText(getString(R.string.percentage_placeholder, progress2));
                percentage1.setText(getString(R.string.percentage_placeholder, progress1));

                progressBar5.setProgress(progress5, true);
                progressBar4.setProgress(progress4, true);
                progressBar3.setProgress(progress3, true);
                progressBar2.setProgress(progress2, true);
                progressBar1.setProgress(progress1, true);

                int totalWeighted_rating = (rating5 * 5) + (rating4 * 4) + (rating3 * 3) + (rating2 * 2) + rating1;
                float rating_float = 0;
                if(totalReviews != 0){
                    rating_float = (float) totalWeighted_rating / totalReviews;
                }
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                tuitionRatingBar.setRating(Float.parseFloat(decimalFormat.format(rating_float)));
                String rating_msg = getString(R.string.rating_msg, decimalFormat.format(rating_float));
                SpannableString rating_span = new SpannableString(rating_msg);
                rating_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tuitionRatingNum.setText(rating_span);

                String noReviews_msg = getString(R.string.totalNo_reviews, String.valueOf(totalReviews));
                SpannableString noReviews_span = new SpannableString(noReviews_msg);
                totalNoReviews.setText(noReviews_span);

                Log.d("Reviews", reviewsList.toString());
                reviewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("filterReviews", "Database error: " + error.getMessage());
            }
        });
    }

    public void onFavouritesClick(View view){
        ImageView fav_icon_empty = findViewById(R.id.fav_emptyIcon);
        fav_icon_empty.setImageResource(R.drawable.icons8_book_and_pencil_96);
        //Implement logic for adding to user's favourites
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

    public String extractAndStitch_subjects(String input) {
        String[] words = input.split(",\\s+");
        StringBuilder stitchedString = new StringBuilder();

        for (String word : words) {
            if (word.length()<=4) {
                String substring = word.toUpperCase();
                stitchedString.append(substring).append(", ");
            } else {
                if(word.contains(" ")){
                    String[] words1 = word.split("\\s+");
                    for(String word1 : words1){
                        String substring1;
                        if(word1.length()<=4){
                            substring1 = word1.toUpperCase();
                        }
                        else{
                            substring1 = word1.substring(0,1).toUpperCase() + word1.substring(1);
                        }

                        stitchedString.append(substring1).append(" ");
                    }
                    stitchedString.deleteCharAt(stitchedString.length()-1);
                }
                else{
                    String substring2 = word.substring(0,1).toUpperCase() + word.substring(1);
                    stitchedString.append(substring2);
                }
                stitchedString.append(", ");
            }
        }

        return stitchedString.deleteCharAt(stitchedString.length()-2).toString().trim();
    }

    public String extractAndStitch(String input) {
        String[] words = input.split(",\\s+");
        StringBuilder stitchedString = new StringBuilder();

        for (String word : words) {
            if (word.length()<=5) {
                String substring = word.toUpperCase();
                stitchedString.append(substring).append(", ");
            } else {
                if(word.contains(" ")){
                    String[] words1 = word.split("\\s+");
                    for(String word1 : words1){
                        String substring1;
                        if(word1.length()<=5){
                            substring1 = word1.toUpperCase();
                        }
                        else{
                            substring1 = word1.substring(0,1).toUpperCase() + word1.substring(1);
                        }

                        stitchedString.append(substring1).append(" ");
                    }
                    stitchedString.deleteCharAt(stitchedString.length()-1);
                }
                else{
                    String substring2 = word.substring(0,1).toUpperCase() + word.substring(1);
                    stitchedString.append(substring2);
                }
                stitchedString.append(", ");
            }
        }

        return stitchedString.deleteCharAt(stitchedString.length()-2).toString().trim();
    }

    public void onReviewClick(View view) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("TuitionCentre");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    TuitionCentre tuitionCentre = dataSnapshot.getValue(TuitionCentre.class);
                    if(tuitionCentre.getId().equals(tuitionCentreId)){
                        TC = tuitionCentre;
                        Intent intent = new Intent(ResultsActivity.this, PostReviewActivity.class);
                        intent.putExtra("tuitionCentre", TC);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}