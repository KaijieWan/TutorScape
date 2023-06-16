package com.example.tutorscape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.tutorscape.Model.TuitionCentre;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class OptionsActivity extends AppCompatActivity {
    private LinearLayout favourites;
    private LinearLayout reviews;
    private LinearLayout settings;
    private LinearLayout logout;
    private FirebaseAuth firebaseAuth;
    private String displayName;
    private String displayEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        favourites = findViewById(R.id.fav_clickable);
        reviews = findViewById(R.id.reviews_clickable);
        settings = findViewById(R.id.settings_clickable);
        logout = findViewById(R.id.logout_clickable);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        Log.d("uid", uid);
        DatabaseReference refName = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users/" + uid + "/name");
        DatabaseReference refEmail = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users/" + uid + "/email");
        refName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    displayName = snapshot.getValue(String.class);
                    Log.d("displayName", displayName);
                } else {
                    Log.d("displayName", "Data does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("displayName", "Data retrieval canceled: " + error.getMessage());
            }
        });
        refEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    displayEmail = snapshot.getValue(String.class);
                    Log.d("displayEmail", displayEmail);
                    //String displayTitle = displayName + "\n" + displayEmail;
                    getSupportActionBar().setTitle(displayName + "\n" + displayEmail);
                } else {
                    Log.d("displayEmail", "Data does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("displayEmail", "Data retrieval canceled: " + error.getMessage());
            }
        });
        /*String displayTitle = displayName + "\n" + displayEmail;
        getSupportActionBar().setTitle(displayTitle);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(OptionsActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
    }
}