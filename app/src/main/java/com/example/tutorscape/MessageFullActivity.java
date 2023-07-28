package com.example.tutorscape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.tutorscape.Model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MessageFullActivity extends AppCompatActivity {
    private String messageID;
    private FirebaseAuth firebaseAuth;
    private TextView messageTitle;
    private TextView messageDate;
    private TextView messageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_full);

        Bundle intent = getIntent().getExtras();
        if(intent != null){
            messageID = intent.getString("messageID");
        }

        messageTitle = findViewById(R.id.message_title);
        messageDate = findViewById(R.id.message_date);
        messageContent = findViewById(R.id.message_content);

        firebaseAuth = FirebaseAuth.getInstance();
        String userID = firebaseAuth.getUid();

        DatabaseReference mRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("Messages/" + userID);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    if(message.getMessageID().equals(messageID)){
                        messageTitle.setText(message.getTitle());
                        messageDate.setText(message.getDate().substring(0, 8));
                        messageContent.setText(message.getContent());

                        //Setting isRead in database to true
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isRead", true);
                        mRef.child(messageID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("mRef update", "isRead set to true");
                            }
                        });
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}