package com.example.tutorscape.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.tutorscape.Model.Notification;
import com.example.tutorscape.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NotificationsFragment extends Fragment {
    static boolean updatesTouched = false;
    static boolean favTouched = false;
    static boolean favCountTouched = false;
    private SwitchCompat updatesSwitch;
    private SwitchCompat favSwitch;
    private SwitchCompat favItemCountSwitch;
    private AppCompatButton timerSetButton;
    private EditText favTimer;
    private LinearLayout favTimerLayout;
    private FirebaseAuth firebaseAuth;
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings_notif, container, false);
        updatesSwitch = view.findViewById(R.id.updates_switch);
        favSwitch = view.findViewById(R.id.fav_reminder_switch);
        favTimer = view.findViewById(R.id.fav_timer);
        favTimerLayout = view.findViewById(R.id.fav_timer_layout);
        favItemCountSwitch = view.findViewById(R.id.fav_items_count_switch);
        timerSetButton = view.findViewById(R.id.setButton);

        favTimerLayout.setVisibility(View.INVISIBLE);
        timerSetButton.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();

        DatabaseReference notifRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference().child("Notifications/" + userId);

        notifRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Notification notification = snapshot.getValue(Notification.class);
                if(notification.isFavCount()){
                    favItemCountSwitch.setChecked(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        updatesSwitch.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updatesTouched = true;
                return false;
            }
        });

        updatesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (updatesTouched) {
                    updatesTouched = false;
                    if (isChecked) {
                        //if set to On, do something
                    }
                    else { //if set to Off, do something
                    }
                }
            }
        });

        favSwitch.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                favTouched = true;
                return false;
            }
        });

        favSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (favTouched) {
                    favTouched = false;
                    if (isChecked) {
                        //if set to On, do something
                        favTimerLayout.setVisibility(View.VISIBLE);
                        favTimer.setText("1");
                        timerSetButton.setVisibility(View.VISIBLE);
                    }
                    else { //if set to Off, do something
                        favTimerLayout.setVisibility(View.INVISIBLE);
                        timerSetButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        favItemCountSwitch.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                favCountTouched = true;
                return false;
            }
        });

        favItemCountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (favCountTouched) {
                    favCountTouched = false;
                    DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference().child("Notifications/" + userId);
                    HashMap<String, Object> map = new HashMap<>();
                    if(isChecked) {
                        //if set to On
                        map.put("favCount", true);

                        ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("favItemCountSwitch", "onCheckedChanged setValue Successful");
                                    Toast.makeText(getContext(), "Favourites count set!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else { //if set to Off
                        map.put("favCount", false);

                        ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("favItemCountSwitch", "onCheckedChanged setValue Successful");
                                    Toast.makeText(getContext(), "Favourites count removed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        return view;
    }
}
