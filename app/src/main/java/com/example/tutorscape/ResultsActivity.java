package com.example.tutorscape;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //Progress/rating bar logic and display is implemented here instead of in the adapter class
        //ProgressBar progressBar1 = findViewById(R.id.progressBar1);
        //ProgressBar progressBar2 = findViewById(R.id.progressBar2);

        // Update progress for each rating
        int rating1 = 4;
        int rating2 = 2;

        int maxRating = 5;  // maximum rating value

        int progress1 = (rating1 * 100) / maxRating;
        int progress2 = (rating2 * 100) / maxRating;

        //progressBar1.setProgress(progress1);
        //progressBar2.setProgress(progress2);
    }

    public void onFavouritesClick(View view){
        ImageView fav_icon_empty = findViewById(R.id.fav_emptyIcon);
        fav_icon_empty.setImageResource(R.drawable.icons8_book_and_pencil_96);
        //Implement logic for adding to user's favourites
    }
}