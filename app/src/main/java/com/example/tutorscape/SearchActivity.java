package com.example.tutorscape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class SearchActivity extends AppCompatActivity {
    private AppBarLayout appBarLayout;
    private FrameLayout frameLayout;
    private TabLayout tab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        appBarLayout = findViewById(R.id.bottom);
        frameLayout = findViewById(R.id.fragment_container);
        tab = findViewById(R.id.top_navigation);
    }
}