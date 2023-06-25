package com.example.tutorscape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorscape.Fragments.ReviewFragment;
import com.example.tutorscape.Model.Review;
import com.example.tutorscape.Model.TuitionCentre;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class EditReviewActivity extends AppCompatActivity {
    private Review review;
    private TextView tuitionName;
    private TextView tuitionAddress;
    private TextView tuitionPostal;
    private TextView tuitionContact;
    private TextView tuitionWebsite;
    private ImageView tuitionImage;
    private RatingBar review_rating;
    private EditText review_subject;
    private EditText review_text;
    private AppCompatButton cancelButton;
    private AppCompatButton submitButton;
    private ProgressBar progressBar;
    private TextView progressText;
    private TuitionCentre tuitionCentre;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        firebaseAuth = FirebaseAuth.getInstance();

        String tuitionCentreId = null;

        //Receive intent for tuition centre to set text
        Intent intent = getIntent();
        if (intent.hasExtra("review")) {
            review = (Review) intent.getSerializableExtra("review");
            tuitionCentreId = intent.getStringExtra("tuitionCentreId");
        }

        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("TuitionCentre");

        String finalTuitionCentreId = tuitionCentreId;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    TuitionCentre TC = dataSnapshot.getValue(TuitionCentre.class);
                    if(TC.getId().equals(finalTuitionCentreId)){
                        tuitionCentre = TC;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        tuitionName = findViewById(R.id.tuition_name);
        tuitionAddress = findViewById(R.id.tuition_address);
        tuitionPostal = findViewById(R.id.tuition_postal);
        tuitionContact = findViewById(R.id.tuition_contact);
        tuitionWebsite = findViewById(R.id.tuition_website);
        tuitionImage = findViewById(R.id.tuition_image);
        review_rating = findViewById(R.id.rating_bar);
        review_subject = findViewById(R.id.review_subject);
        review_text = findViewById(R.id.review_text);
        cancelButton = findViewById(R.id.cancel_button);
        submitButton = findViewById(R.id.submit_button);
        progressBar = findViewById(R.id.progressbar);
        progressText = findViewById(R.id.progressText);

        progressBar.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(EditReviewActivity.this, ReviewFragment.class);
                startActivity(newIntent);
                finish();
            }
        });

        ResultsActivity RA = new ResultsActivity();
        String address_msg = getString(R.string.address_msg, RA.capitalizeAfterSpace(tuitionCentre.getAddress()));
        String postal_msg = getString(R.string.postal_msg, tuitionCentre.getPostal());
        String contact_msg = getString(R.string.contact_msg, tuitionCentre.getContactNo());
        String website_msg = getString(R.string.website_msg, tuitionCentre.getWebsite());

        SpannableString address_span = new SpannableString(address_msg);
        address_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString postal_span = new SpannableString(postal_msg);
        postal_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString contact_span = new SpannableString(contact_msg);
        contact_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString website_span = new SpannableString(website_msg);
        website_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tuitionName.setText(RA.capitalizeAfterSpace(tuitionCentre.getName()));
        tuitionAddress.setText(address_span);
        tuitionPostal.setText(postal_span);
        tuitionContact.setText(contact_span);
        tuitionWebsite.setText(website_span);
        Picasso.get().load(tuitionCentre.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(tuitionImage);

        review_rating.setRating(Float.parseFloat(review.getRating_num()));
        review_subject.setText(review.getSubjects_enrolled());
        review_text.setText(review.getReview_text());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(review_rating.getRating(), review_subject.getText().toString(), review_text.getText().toString(), review.getId());
            }
        });
    }

    private void upload(float rating, String txt_subject, String txt_review_text, String reviewId) {
        DecimalFormat decimalFormat = new DecimalFormat("#");
        String ratingNum = decimalFormat.format(rating);

        if(TextUtils.isEmpty(txt_subject) || TextUtils.isEmpty(txt_review_text)){
            Toast.makeText(EditReviewActivity.this, "Empty Values!", Toast.LENGTH_SHORT).show();
        }
        else{
            DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Reviews" + reviewId);
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            HashMap<String, Object> updates = new HashMap<>();
            updates.put("/TCID", tuitionCentre.getId());
            updates.put("/UID", firebaseAuth.getUid());
            updates.put("/id", reviewId);
            updates.put("/rating_num", ratingNum);
            updates.put("/review_date", getCurrentDateTime());
            updates.put("/review_text", txt_review_text);
            updates.put("/subjects_enrolled", txt_subject);
            updates.put("/isEdited", true);

            ref.child(reviewId).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.GONE);
                    progressText.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if(task.isSuccessful()){
                        Toast.makeText(EditReviewActivity.this, "Review updated!", Toast.LENGTH_SHORT).show();
                        Intent backIntent = new Intent(EditReviewActivity.this, ReviewFragment.class);
                        startActivity(backIntent);
                        Log.d("EditReviewActivity", "Starting ReviewFragment");
                        finish();
                    }
                    else{
                        Log.d("EditReviewActivity", "setValue failed: " + task.getException().getMessage());
                        Toast.makeText(EditReviewActivity.this, "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public String getCurrentDateTime() {
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        // Get the current date and time
        Date currentDate = new Date();

        // Format the date and time using the SimpleDateFormat object
        // Now you can use the formattedDateTime string as per your requirements
        return dateFormat.format(currentDate);
    }
}