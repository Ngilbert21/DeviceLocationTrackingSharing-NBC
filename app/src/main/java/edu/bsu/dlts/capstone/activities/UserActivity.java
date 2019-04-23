package edu.bsu.dlts.capstone.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.bsu.dlts.capstone.R;

public abstract class UserActivity extends AppCompatActivity {

    public EditText firstName;
    public EditText lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        Button addUser = findViewById(R.id.addUser);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserActivity.this.onClick();
            }
        });
    }

    public abstract void onClick();
}
