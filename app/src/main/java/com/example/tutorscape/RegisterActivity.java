package com.example.tutorscape;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText enterDOB;
    private Button register;
    private TextView exist_login;
    private EditText email;
    private EditText name;
    private EditText mobile;
    private EditText password;
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
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

        exist_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
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
            EditText enterAge = getActivity().findViewById(R.id.enterDOB);
            enterAge.setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month + 1, year));
        }
    }
}