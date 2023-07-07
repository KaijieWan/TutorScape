package com.example.tutorscape.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.tutorscape.R;

public class NotificationsFragment extends Fragment {
    static boolean updatesTouched = false;
    static boolean favTouched = false;
    private SwitchCompat updatesSwitch;
    private SwitchCompat favSwitch;
    private EditText favTimer;
    private LinearLayout favTimerLayout;
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

        favTimerLayout.setVisibility(View.GONE);

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
                    }
                    else { //if set to Off, do something
                        favTimerLayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        return view;
    }
}
