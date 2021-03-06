package edu.bsu.dlts.capstone.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import edu.bsu.dlts.capstone.R;

/**
 * This class is not used in the current code
 */
public class InvitationActivity extends AppCompatActivity {

    private String currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this,currentGroupName,Toast.LENGTH_SHORT).show();
    }

}
