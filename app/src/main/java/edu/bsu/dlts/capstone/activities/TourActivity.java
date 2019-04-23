package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.bsu.dlts.capstone.R;

public class TourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);
        SharedPreferences pref = getSharedPreferences("user", 0);
        TextView user = findViewById(R.id.user);
        String userStr = pref.getString("firstName", "") + " " + pref.getString("lastName", "");
        user.setText(userStr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configureGroupButton();
        configureBeginTourButton();
    }

    private void configureGroupButton(){
        Button findGroups = findViewById(R.id.findGroups);
        findGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toGroup = new Intent(TourActivity.this, BrandNewGroupActivity.class);
                startActivity(toGroup);
            }
        });
    }

    private void configureBeginTourButton(){
        Button tour = findViewById(R.id.tour);
        tour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toTours = new Intent(TourActivity.this, MapsActivity.class);
                startActivity(toTours);
            }
        });
    }
}
