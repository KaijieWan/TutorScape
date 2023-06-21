package com.example.tutorscape;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class PostReviewActivity extends AppCompatActivity {
    private TextView tuitionName;
    private TextView tuitionAddress;
    private TextView tuitionPostal;
    private TextView tuitionContact;
    private TextView tuitionWebsite;
    private ImageView tuitionImage;
    private RatingBar review_rating;
    private EditText review_subject;
    private EditText review_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);

    }
}