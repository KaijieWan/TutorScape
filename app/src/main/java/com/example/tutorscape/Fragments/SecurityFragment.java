package com.example.tutorscape.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.tutorscape.EditReviewActivity;
import com.example.tutorscape.MainActivity;
import com.example.tutorscape.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SecurityFragment extends Fragment {
    private EditText currentEmail;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private AppCompatButton submitButton;
    private AppCompatButton cancelButton;
    private AppCompatButton deleteButton;
    private FirebaseAuth firebaseAuth;
    private String curEmail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings_security, container, false);

        currentEmail = view.findViewById(R.id.currentEmail);
        currentPassword = view.findViewById(R.id.currentPassword);
        newPassword = view.findViewById(R.id.newPassword);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        submitButton = view.findViewById(R.id.submit_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        deleteButton = view.findViewById(R.id.delete_button);

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();

        DatabaseReference emailRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users/" + userId + "/email");

        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 curEmail = snapshot.getValue(String.class);
                 Log.d("Security fragment - email listener event", "email: " + curEmail);
                 currentEmail.setText(curEmail);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseUser user = firebaseAuth.getCurrentUser();


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(currentPassword.getText().toString())){
                    Toast.makeText(getContext(), "Current password is required for any form of update!", Toast.LENGTH_SHORT).show();
                }
                else{
                    AuthCredential credential = EmailAuthProvider.getCredential(curEmail, currentPassword.getText().toString());
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //User does not want to change password
                            if(TextUtils.isEmpty(newPassword.getText().toString()) || TextUtils.isEmpty(confirmPassword.getText().toString())){
                                updateUserEmail();
                            }
                            else{ //User wants to change password
                                if(!newPassword.getText().toString().equals(confirmPassword.getText().toString())){
                                    Toast.makeText(getContext(), "Passwords do not match, please enter again!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    updateUserEmailPass();
                                    Toast.makeText(getContext(), "Email address and password update successful!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPassword.getText().clear();
                newPassword.getText().clear();
                confirmPassword.getText().clear();

                emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String fixedEmail = snapshot.getValue(String.class);
                        currentEmail.setText(fixedEmail);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d("Security fragment", "Account deleted");
                            //Update realtime database as well (remove account)
                            DatabaseReference delRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference("Users/" + userId);
                            delRef.removeValue();

                            Toast.makeText(getContext(), "Account successfully deleted!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        return view;
    }

    private void updateUserEmailPass() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d("updateUserEmailPass", "UID: " + firebaseAuth.getUid());

        user.updateEmail(currentEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("updateUserEmailPass", "Email update successful");
                    //Update realtime database as well
                    String userId = firebaseAuth.getUid();
                    DatabaseReference emailRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("Users/" + userId);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("email", currentEmail.getText().toString());

                    emailRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("updateUserEmailPass", "update successful");
                            }
                            else{
                                Log.d("updateUserEmailPass", "update failed: " + task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });

        user.updatePassword(confirmPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("updateUserEmailPass", "Password update successful");
                }
            }
        });
    }

    private void updateUserEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        user.updateEmail(currentEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("updateUserEmail", "Successful");
                    Toast.makeText(getContext(), "Email address update successful!", Toast.LENGTH_SHORT).show();
                    //Update realtime database as well
                    String userId = firebaseAuth.getUid();
                    DatabaseReference emailRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("Users/" + userId);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("email", currentEmail.getText().toString());

                    emailRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("updateUserEmailPass", "update successful");
                            }
                            else{
                                Log.d("updateUserEmailPass", "update failed: " + task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });

    }
}
