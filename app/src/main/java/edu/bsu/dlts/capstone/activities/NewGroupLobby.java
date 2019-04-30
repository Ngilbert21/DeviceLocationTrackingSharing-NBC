package edu.bsu.dlts.capstone.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.Objects;

import edu.bsu.dlts.capstone.R;

/**
 * This class is not used in the current code
 */
public class NewGroupLobby extends AppCompatActivity {
    private Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_lobby);

        Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();

        mToolbar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Group Lobby");

    }
}
