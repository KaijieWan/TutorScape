package com.example.tutorscape;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText enterDOB;
    private static String dateOfBirth;
    private Button register;
    private TextView exist_login;
    private EditText email;
    private EditText name;
    private EditText mobile;
    private EditText password;

    private FirebaseAuth auth;
    private DatabaseReference rootRef;
    ProgressBar progressBar;
    TextView progressText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        enterDOB = findViewById(R.id.enterDOB);
        register = findViewById(R.id.register_button);
        exist_login = findViewById(R.id.existing_login);
        email = findViewById(R.id.editTextTextEmailAddress);
        name = findViewById(R.id.editTextName);
        mobile = findViewById(R.id.enterMobile);
        password = findViewById(R.id.enterPass);
        progressBar = findViewById(R.id.progressbar);
        progressText = findViewById(R.id.progressText);

        auth = FirebaseAuth.getInstance();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(10000000);

        rootRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        Log.d("RegisterActivity", "database reference: " + rootRef);

        progressBar.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);

        enterDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_name = name.getText().toString();
                String txt_mobile = mobile.getText().toString();

                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_name) || TextUtils.isEmpty(txt_mobile)){
                    Toast.makeText(RegisterActivity.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(txt_email, txt_password, txt_name, txt_mobile, dateOfBirth);
                }
            }
        });

        exist_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                //getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
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
            EditText enterAge = getActivity().findViewById(R.id.enterDOB);
            enterAge.setText(dateOfBirth);
        }
    }

    private void registerUser(String email, String password, String name, String mobile, String DOB) {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("mobile", mobile);
                map.put("Date of Birth", DOB);
                map.put("id", auth.getCurrentUser().getUid());

                rootRef.child("Users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Log.d("RegisterActivity", "setValue onComplete");
                        if(task.isSuccessful()){
                            DatabaseReference notifRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                                    .getReference().child("Notifications/" + auth.getCurrentUser().getUid());
                            HashMap<String, Object> update = new HashMap<>();
                            update.put("favCount", true);
                            update.put("messageCount", true);
                            notifRef.updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("RegisterActivity", "Notifications setValue completed");
                                    }
                                }
                            });

                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, SearchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            Log.d("RegisterActivity", "Starting SearchActivity");
                            finish();
                        }
                        else{
                            Log.d("RegisterActivity", "setValue failed: " + task.getException().getMessage());
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("RegisterActivity", "Registration failed", e);
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}