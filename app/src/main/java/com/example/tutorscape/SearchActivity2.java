package com.example.tutorscape;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.tutorscape.Adapter.DrawerAdapter;
import com.example.tutorscape.Fragments.DashboardFragment;
import com.example.tutorscape.Fragments.FavFragment;
import com.example.tutorscape.Fragments.ReviewFragment;
import com.example.tutorscape.Fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class SearchActivity2 extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{
    private static final int POS_CLOSE = 0;
    private static final int POS_DASHBOARD = 1;
    private static final int POS_FAVOURITES = 2;
    private static final int POS_REVIEWS = 3;
    private static final int POS_SETTINGS = 4;
    private static final int POS_LOGOUT = 6;
    private FirebaseAuth firebaseAuth;
    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

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
        //menu = findViewById(R.id.menu_image);

        firebaseAuth = FirebaseAuth.getInstance();
        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(myToolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CLOSE),
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_FAVOURITES),
                createItemFor(POS_REVIEWS),
                createItemFor(POS_SETTINGS),
                new SpaceItem(260),
                createItemFor(POS_LOGOUT)
        ));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);

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

        /*menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity2.this, OptionsActivity.class));
            }
        });*/
    }

    private DrawerItem createItemFor(int position){
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.techBlue))
                .withTextTint(color(R.color.black))
                .withSelectedIconTint(color(R.color.techBlue))
                .withSelectedTextTint(color(R.color.techBlue));
    }

    @ColorInt
    private int color(@ColorRes int res){
        return ContextCompat.getColor(this, res);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for(int i = 0; i < ta.length(); i++){
            int id = ta.getResourceId(i, 0);
            if(id!=0){
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }



    @Override
    public void onBackPressed() {
        /*if (exitApp) {
            finish(); // Exit the app
        } else {
            super.onBackPressed(); // Allow navigating back within fragments
        }*/
        finish();
    }

    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(position == POS_DASHBOARD){
            DashboardFragment dashboardFragment = new DashboardFragment();
            transaction.replace(R.id.container, dashboardFragment);
        }
        else if(position == POS_FAVOURITES){
            FavFragment favFragment = new FavFragment();
            transaction.replace(R.id.container, favFragment);
        }
        else if(position == POS_REVIEWS){
            ReviewFragment reviewFragment = new ReviewFragment();
            transaction.replace(R.id.container, reviewFragment);
        }
        else if(position == POS_SETTINGS){
            SettingsFragment settingsFragment = new SettingsFragment();
            transaction.replace(R.id.container, settingsFragment);
        }
        else if(position == POS_LOGOUT){
            firebaseAuth.signOut();
            startActivity(new Intent(SearchActivity2.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

        slidingRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }
}