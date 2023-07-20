package com.example.tutorscape;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.tutorscape.Adapter.DrawerAdapter;
import com.example.tutorscape.Fragments.DashboardFragment;
import com.example.tutorscape.Fragments.FavFragment;
import com.example.tutorscape.Fragments.ReviewFragment;
import com.example.tutorscape.Fragments.SettingsFragment;
import com.example.tutorscape.Model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransferActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {
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
    private DrawerAdapter adapter;
    private Toolbar toolbar;
    private int favoritesCount = 0; // Placeholder count for favorites
    private ImageView adminMessage;
    private NavHostFragment navHostFragment;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        firebaseAuth = FirebaseAuth.getInstance();

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        adapter = new DrawerAdapter(Arrays.asList(
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

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();

        DatabaseReference notifRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Notifications/" + userId);

        notifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Notification notification = snapshot.getValue(Notification.class);
                boolean isFavCountEnabled = notification != null && notification.isFavCount();
                Log.d("TAG", "Notification settings retrieved. isFavCountEnabled: " + isFavCountEnabled);
                if(isFavCountEnabled){
                    // Retrieve the favorites count from the database initially
                    DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference().child("Favourites/" + userId);

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            favoritesCount = (int) snapshot.getChildrenCount();
                            Log.d("TAG", "Favorites count retrieved: " + favoritesCount);
                            // After retrieving the count, update the drawer menu item
                            //updateFavoritesCountInDrawer();
                            updateDrawerMenuItems();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle cancellation
                            Log.e("TAG", "Favorites count retrieval cancelled.");
                        }
                    });
                }
                else{
                    //Handle the disappearing of the number banner
                    Log.d("TAG", "Favorites count is not enabled. Handling disappearing of number banner.");
                    favoritesCount = 0;
                    //updateFavoritesCountInDrawer();
                    updateDrawerMenuItems();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        adminMessage = findViewById(R.id.admin_message);
        adminMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransferActivity.this, AdminMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateFavoritesCountInDrawer() {
        Log.d("TAG", "Updating favorites count in the drawer: " + favoritesCount);
        // Update the favorites count in the drawer menu item
        DrawerItem favoritesItem = adapter.getItem(POS_FAVOURITES);
        if (favoritesItem instanceof SimpleItem) {
            ((SimpleItem) favoritesItem).setFavCount(favoritesCount);
            adapter.notifyItemChanged(POS_FAVOURITES);
        }
    }

    private void updateDrawerMenuItems() {
        // Iterate through all the items in the drawer menu
        for (int i = 0; i < adapter.getItemCount(); i++) {
            DrawerItem item = adapter.getItem(i);
            if (item instanceof SimpleItem simpleItem) {
                if (i == POS_FAVOURITES) {
                    // Update the favorites count for the "Favorites" item
                    simpleItem.setFavCount(favoritesCount);
                }
                // Determine the visibility of the number banner based on the item position
                if (i == POS_FAVOURITES && favoritesCount > 0){
                    simpleItem.setNumBannerVisible(true);
                } else {
                    simpleItem.setNumBannerVisible(false);
                }
                // Notify the adapter about the change
                adapter.notifyItemChanged(i);
            }
        }
    }

    private DrawerItem createItemFor(int position){

        return new SimpleItem(screenIcons[position], screenTitles[position], position)
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
        finish();
    }

    @Override
    public void onItemSelected(int position) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        if(position == POS_DASHBOARD){
            DashboardFragment dashboardFragment = new DashboardFragment();
            transaction.replace(R.id.container, dashboardFragment);
            //navController.navigate(R.id.DashboardFragment);
        }
        else if(position == POS_FAVOURITES){
            //Define the database reference for the reviews and pass in the count
            //firebaseAuth = FirebaseAuth.getInstance();
            String userId = firebaseAuth.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference().child("Favourites/" + userId);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = (int) snapshot.getChildrenCount();
                    DrawerItem selectedItem = adapter.getItem(position);
                    if(selectedItem instanceof SimpleItem){
                        ((SimpleItem) selectedItem).setFavCount(count);
                        adapter.notifyItemChanged(position);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            FavFragment favFragment = new FavFragment();
            transaction.replace(R.id.container, favFragment);
            //navController.navigate(R.id.FavFragment);
        }
        else if(position == POS_REVIEWS){
            ReviewFragment reviewFragment = new ReviewFragment();
            transaction.replace(R.id.container, reviewFragment);
            //navController.navigate(R.id.ReviewFragment);
        }
        else if(position == POS_SETTINGS){
            SettingsFragment settingsFragment = new SettingsFragment();
            transaction.replace(R.id.container, settingsFragment);
            //navController.navigate(R.id.SettingsFragment);
        }
        else if(position == POS_LOGOUT){
            firebaseAuth.signOut();
            startActivity(new Intent(TransferActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

        slidingRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }
}