package edu.bsu.dlts.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class TourActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configureGroupButton();
        configureBeginTourButton();
    }

    private void configureGroupButton(){
        Button FindGroups = (Button) findViewById(R.id.button14);
        FindGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(TourActivity.this, GroupActivity.class);
                startActivity(intent3);
            }
        });
    }

    private void configureBeginTourButton(){
        Button Tour = (Button) findViewById(R.id.button10);
        Tour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent(TourActivity.this, MapsActivity.class);
                startActivity(intent6);
            }
        });
    }
}
