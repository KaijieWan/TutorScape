package com.example.tutorscape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SearchActivity2 extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private NavHostFragment navHostFragment;
    private Toolbar myToolbar;
    private boolean exitApp = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        navController = navHostFragment.getNavController();
        //navController = Navigation.findNavController(this, R.id.fragment_container);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        // Set click listener for the menu items
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                exitApp = true;
                if (item.getItemId() == R.id.nav_map) {
                    navController.navigate(R.id.MapFragment);
                    return true;
                }
                if (item.getItemId() == R.id.nav_search) {
                    navController.navigate(R.id.SearchFragment);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (exitApp) {
            finish(); // Exit the app
        } else {
            super.onBackPressed(); // Allow navigating back within fragments
        }
    }
}