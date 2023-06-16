package com.example.tutorscape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class SearchActivity2 extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private NavHostFragment navHostFragment;
    private Toolbar myToolbar;
    private boolean exitApp = false;
    private ImageView menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);
        menu = findViewById(R.id.menu_image);

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
                if (item.getItemId() == R.id.nav_updates) {
                    navController.navigate(R.id.UpdatesFragment);
                    return true;
                }
                if (item.getItemId() == R.id.nav_notif) {
                    navController.navigate(R.id.NotifFragment);
                    return true;
                }
                return false;
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity2.this, OptionsActivity.class));
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