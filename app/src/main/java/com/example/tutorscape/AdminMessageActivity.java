package com.example.tutorscape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Contacts;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

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
    private FirebaseAuth firebaseAuth;
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

        firebaseAuth = FirebaseAuth.getInstance();
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
                        userId.setVisibility(View.GONE);
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
                finish();
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
                            .child("Messages");
                    String messageID = massRef.push().getKey();
                    DatabaseReference userRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                            .child("Users");

                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String UID = dataSnapshot.getKey();
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("UID", "");
                                map.put("date", getCurrentDateTime());
                                map.put("title", messageTitle.getText().toString());
                                map.put("content", messageContent.getText().toString());
                                map.put("isRead", false);
                                map.put("messageID", messageID);
                                massRef.child(UID).child(messageID).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("sendButton", "mass message data send and set");
                                        Toast.makeText(AdminMessageActivity.this, "Mass message sent!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if (privateCheck.isChecked()) {
                    DatabaseReference privateRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                            .child("Messages");
                    DatabaseReference userRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                            .child("Users");

                    //Have to touch up again
                    HashMap<String, Object> map = new HashMap<>();
                    String messageID = privateRef.push().getKey();
                    map.put("UID", userId.getText().toString());
                    map.put("date", getCurrentDateTime());
                    map.put("title", messageTitle.getText().toString());
                    map.put("content", messageContent.getText().toString());
                    map.put("isRead", false);
                    map.put("messageID", messageID);

                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String UID = dataSnapshot.getKey();
                                privateRef.child(UID).child(messageID).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("sendButton", "private message data send and set");
                                        Toast.makeText(AdminMessageActivity.this, "Private message sent!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
    }

    public String getCurrentDateTime() {
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        // Get the current date and time
        Date currentDate = new Date();

        // Format the date and time using the SimpleDateFormat object
        // Now you can use the formattedDateTime string as per your requirements
        return dateFormat.format(currentDate);
    }
}