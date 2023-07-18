package com.example.tutorscape;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class AdminMessageActivity extends AppCompatActivity {
    private CheckBox massCheck;
    private CheckBox privateCheck;
    private EditText messageTitle;
    private EditText messageContent;
    private AppCompatButton cancelButton;
    private AppCompatButton sendButton;
    private static boolean isMass = false;
    private static boolean massTouched = false;
    private static boolean privateTouched = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_message);

        massCheck = findViewById(R.id.massCheck);
        privateCheck = findViewById(R.id.privateCheck);
        messageTitle = findViewById(R.id.message_title);
        messageContent = findViewById(R.id.message_content);
        cancelButton = findViewById(R.id.cancel_button);
        sendButton = findViewById(R.id.send_button);

        massCheck.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                massTouched = true;
                return false;
            }
        });

        massCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (massTouched) {
                    massTouched = false;
                    if (isChecked) {
                        //if checked, do something
                        isMass = true;
                        privateCheck.setChecked(true);
                    }
                    else { //if unchecked, do something
                    }
                }
            }
        });
    }
}