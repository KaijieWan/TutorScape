package com.example.tutorscape;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.tutorscape.databinding.ActivitySearchBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //FirebaseDatabase.getInstance().getReference().child("Updates").child("Fees").setValue("Mindwork tuition has raised fees by $5");
        //HashMap<String, Object> map = new HashMap<>();
        //map.put("Name", "Wan Kai Jie");
        //map.put("Email", "kaijie@gmail.com");

        //FirebaseDatabase.getInstance().getReference().child("Account").child("random").updateChildren(map);
    }
}