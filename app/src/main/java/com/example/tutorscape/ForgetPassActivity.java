package com.example.tutorscape;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ForgetPassActivity extends AppCompatActivity {

    private TextView exist_login;
    private TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        exist_login = findViewById(R.id.existing_login2);
        register = findViewById(R.id.register2);

        exist_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgetPassActivity.this, MainActivity.class));
                //getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgetPassActivity.this, RegisterActivity.class));
                //getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
            }
        });
    }
}