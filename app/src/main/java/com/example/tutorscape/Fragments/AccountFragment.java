package com.example.tutorscape.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.tutorscape.EditReviewActivity;
import com.example.tutorscape.R;
import com.example.tutorscape.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AccountFragment extends Fragment {
    private EditText fullName;
    private EditText DOB;
    private EditText mobile;
    private static String dateOfBirth;
    private FirebaseAuth firebaseAuth;
    private AppCompatButton submitButton;
    private AppCompatButton cancelButton;
    private String name;
    private String dob;
    private String hpNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings_account, container, false);

        fullName = view.findViewById(R.id.editName);
        DOB = view.findViewById(R.id.editAge);
        mobile = view.findViewById(R.id.editMobile);
        submitButton = view.findViewById(R.id.submit_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();

        DatabaseReference nameRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users/" + userId + "/name");

        DatabaseReference dobRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users/" + userId + "/Date of Birth");

        DatabaseReference mobileRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users/" + userId + "/mobile");


        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.getValue(String.class);
                fullName.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dob = snapshot.getValue(String.class);
                dateOfBirth = dob;
                DOB.setText(dob);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mobileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hpNumber = snapshot.getValue(String.class);
                mobile.setText(hpNumber);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getChildFragmentManager(), "datePicker");
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(userId, fullName.getText().toString(), dateOfBirth, mobile.getText().toString());
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName.setText(name);
                DOB.setText(dob);
                mobile.setText(hpNumber);
            }
        });

        return view;
    }

    private void upload(String userId, String name, String dob, String hpNum) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users" + "/" + userId);

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("Date of Birth", dob);
        updates.put("mobile", hpNum);

        ref.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Account details updated!", Toast.LENGTH_SHORT).show();
                    fullName.setText(name);
                    DOB.setText(dob);
                    mobile.setText(hpNum);
                }
                else{
                    Log.d("EditReviewActivity", "setValue failed: " + task.getException().getMessage());
                    Toast.makeText(getContext(), "Update unsuccessful!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //pop-up for date selection
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, -100); // 100 years ago
            long minDate = c.getTimeInMillis();
            c.add(Calendar.YEAR, 100); // 100 years from now
            long maxDate = c.getTimeInMillis();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(minDate);
            datePickerDialog.getDatePicker().setMaxDate(maxDate);

            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // Set the text of the enter age field to the selected date
            dateOfBirth = String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month + 1, year);
            EditText editAge = getActivity().findViewById(R.id.editAge);
            editAge.setText(dateOfBirth);
        }
    }
}
