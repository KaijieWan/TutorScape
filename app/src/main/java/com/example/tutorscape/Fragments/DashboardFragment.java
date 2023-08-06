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
import com.example.tutorscape.Model.Message;
import com.example.tutorscape.Model.Notification;
import com.example.tutorscape.R;
import com.example.tutorscape.SearchActivity2;
import com.example.tutorscape.TransferActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class DashboardFragment extends Fragment {
    private NavController navController;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private NavHostFragment navHostFragment;
    private Toolbar myToolbar;
    private FirebaseAuth firebaseAuth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.dashboard_fragment, container, false);
        Log.d("DashboardFragment", "Called");
        //startActivity(new Intent(getContext(), SearchActivity2.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.fragment_container);
        navController = navHostFragment.getNavController();
        //navController = Navigation.findNavController(this, R.id.fragment_container);
        bottomNavigationViewEx = (BottomNavigationViewEx) root.findViewById(R.id.bnve);
        NavigationUI.setupWithNavController(bottomNavigationViewEx, navController);
        Log.d("DashboardFragment", "Second call");

        // Set click listener for the menu items
        // Add badge to an item (e.g., the first item):
        /*BadgeItem badge = new BadgeItem()
                .setBorderWidth(4)
                .setBackgroundColorResource(R.color.colorAccent)
                .setText("3") // Set the number you want to display
                .setTextColorResource(R.color.colorWhite);*/
        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();
        DatabaseReference notifRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Notifications/" + userId);
        DatabaseReference messageRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("Messages/" + userId);

        notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Notification notification = snapshot.getValue(Notification.class);
                    if(notification.isMessageCount()){
                        messageRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int messageCount = 0;
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    Message message = dataSnapshot.getValue(Message.class);
                                    if(!message.isRead()){
                                        messageCount++;
                                    }
                                }
                                BadgeDrawable badgeNotif =  bottomNavigationViewEx.getOrCreateBadge(R.id.nav_notif);
                                badgeNotif.setBadgeTextColor(getResources().getColor(R.color.techBlue, null));
                                badgeNotif.setTextAppearance(R.style.badgeTextAppearance);
                                if(messageCount>0){
                                    badgeNotif.setNumber(messageCount);
                                }
                                else{
                                    bottomNavigationViewEx.removeBadge(R.id.nav_notif);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


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
