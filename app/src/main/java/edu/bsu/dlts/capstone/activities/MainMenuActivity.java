package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import edu.bsu.dlts.capstone.R;

public class MainMenuActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        configureTourButton();
        configureSelectTripButton();
        configureBrowseToursButton();
        configureSettingsButton();
        configureLogOutButton();
    }

    /**
     * All of the configure methods in here set a button up to link to the appropriate activity
     */
    private void configureTourButton(){
        Button createTrip = findViewById(R.id.createTrip);
        createTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toTrip = new Intent(MainMenuActivity.this, NewTripsActivity.class);
                startActivity(toTrip);
            }
        });
    }

    private void configureSelectTripButton(){
        Button selectTrip = findViewById(R.id.selectTrip);
        selectTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSelect = new Intent(MainMenuActivity.this, SelectTripActivity.class);
                startActivity(toSelect);
            }
        });
    }

    private void configureBrowseToursButton(){
        Button prevTour = findViewById(R.id.prevTour);
        prevTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPrevTours = new Intent(MainMenuActivity.this, PreviousToursActivity.class);
                startActivity(toPrevTours);
            }
        });
    }

    private void configureSettingsButton() {
        Button settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSettings = new Intent(MainMenuActivity.this, UpdateUserActivity.class);
                startActivity(toSettings);
            }
        });
    }

    private void configureLogOutButton(){
        Button logOut = findViewById(R.id.logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                FirebaseAuth.getInstance().signOut();
                Intent toLogOut = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(toLogOut);
            }
        });
    }

    /**
     * Signs the user out of Google and the app
     */
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

}
