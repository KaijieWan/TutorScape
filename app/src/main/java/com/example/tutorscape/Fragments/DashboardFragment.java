package com.example.tutorscape.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.tutorscape.MainActivity;
import com.example.tutorscape.R;
import com.example.tutorscape.SearchActivity2;
import com.example.tutorscape.TransferActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class DashboardFragment extends Fragment {
    private NavController navController;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private NavHostFragment navHostFragment;
    private Toolbar myToolbar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.dashboard_fragment, container, false);
        Log.d("DashboardFragment", "Called");
        //startActivity(new Intent(getContext(), SearchActivity2.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.fragment_container);
        navController = navHostFragment.getNavController();
        //navController = Navigation.findNavController(this, R.id.fragment_container);
        bottomNavigationViewEx = root.findViewById(R.id.bnve);
        NavigationUI.setupWithNavController(bottomNavigationViewEx, navController);
        Log.d("DashboardFragment", "Second call");

        // Set click listener for the menu items
        bottomNavigationViewEx.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

        return root;
    }
}
