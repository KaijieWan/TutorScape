package com.example.tutorscape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminMessageActivity extends AppCompatActivity {
    private CheckBox massCheck;
    private CheckBox privateCheck;
    private EditText messageTitle;
    private EditText messageContent;
    private EditText userId;
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
        userId = findViewById(R.id.userId);
        cancelButton = findViewById(R.id.cancel_button);
        sendButton = findViewById(R.id.send_button);

        userId.setVisibility(View.GONE);

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
                        privateCheck.setChecked(false);
                    }
                    else { //if unchecked, do something
                        isMass = false;
                    }
                }
            }
        });

        privateCheck.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                privateTouched = true;
                return false;
            }
        });

        privateCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (privateTouched) {
                    privateTouched = false;
                    if (isChecked) {
                        //if checked, do something
                        isMass = false;
                        massCheck.setChecked(false);
                        userId.setVisibility(View.VISIBLE);
                    }
                    else { //if unchecked, do something
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!massCheck.isChecked() && !privateCheck.isChecked()){
                    Toast.makeText(AdminMessageActivity.this, "Either checkbox must be checked!", Toast.LENGTH_SHORT).show();
                }
                else if (privateCheck.isChecked() && TextUtils.isEmpty(userId.getText().toString())) {
                    Toast.makeText(AdminMessageActivity.this, "User ID to send message to is not specified!", Toast.LENGTH_SHORT).show();
                }
                else if (massCheck.isChecked()) {
                    DatabaseReference massRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                            .child("Message");

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("UID", "");
                    //map.put("date", );
                    map.put("title", messageTitle.getText().toString());
                    map.put("content", messageContent.getText().toString());
                    map.put("isRead", false);
                    map.put("messageID", massRef.push().getKey());

                    massRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Do something once completed
                        }
                    });
                }
            }
        });
    }
}