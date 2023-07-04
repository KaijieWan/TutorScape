package com.example.tutorscape.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

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

public class SecurityFragment extends Fragment {
    private EditText currentEmail;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private AppCompatButton submitButton;
    private AppCompatButton cancelButton;
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

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users/" + userId + "/email");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
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

        AuthCredential credential = EmailAuthProvider.getCredential(curEmail, currentPassword.getText().toString());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //User does not want to update password
                if(TextUtils.isEmpty(newPassword.getText().toString()) || TextUtils.isEmpty(confirmPassword.getText().toString())){
                    //Update with whatever is in the email field
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Execute the update to realtime database
                            //user.updateEmail();

                        }
                    });
                }
                else{ //User wants to update password
                    //Update password along with whatever is in the email field
                }
            }
        });

        return view;
    }
}
