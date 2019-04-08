package edu.bsu.dlts.capstone.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.bsu.dlts.capstone.R;

public class TripsActivity extends AppCompatActivity {
    private String currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
    }
}
