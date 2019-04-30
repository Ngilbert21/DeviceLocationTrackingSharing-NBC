package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.bsu.dlts.capstone.adapters.AzureServiceAdapter;
import edu.bsu.dlts.capstone.interfaces.AsyncExistsResponse;
import edu.bsu.dlts.capstone.models.Person;
import edu.bsu.dlts.capstone.R;

/**
 * A login screen that offers login via Google Sign-In.
 */
public class LoginActivity extends AppCompatActivity implements AsyncExistsResponse {

    private static final int RC_SIGN_IN = 1;

    private GoogleApiClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivity";

    private AzureTableRetriever tableRetriever;

    String clientId = "116675120255-khjkad5oj36hl80huiqfos8hbgge3vhe.apps.googleusercontent.com";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "You Got An Error", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton mGoogleBtn = findViewById(R.id.googleBtn);
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mProgressView = findViewById(R.id.login_progress);

        try{
            AzureServiceAdapter.getInstance();
        } catch (IllegalStateException e){
            AzureServiceAdapter.Initialize(this);
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userEmail = user.getEmail();
                            Log.d(TAG, userEmail);

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("user", 0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("email", userEmail);
                            editor.apply();

                            LoginActivity.TableParams params = new LoginActivity.TableParams(userEmail);

                            tableRetriever = new AzureTableRetriever();
                            tableRetriever.delegate = LoginActivity.this;

                            tableRetriever.execute(params);

                            mProgressView.setVisibility(View.VISIBLE);

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    /**
     * Redirects based on whether or not the user is in the database
     * @param output - the person, or lack thereof, that matches the user
     */
    @Override
    public void processFinish(Person output) {
        mProgressView.setVisibility(View.INVISIBLE);
        if (output.getMId() != null){
            SharedPreferences pref = getSharedPreferences("user", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("id", output.getMId());
            editor.apply();

            startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
        } else{
            startActivity(new Intent(LoginActivity.this, NewUserActivity.class));
        }
    }

    /**
     * Retrieves the person associated with the user
     */
    public static class AzureTableRetriever extends AsyncTask<TableParams, Void, Person> {
        public AsyncExistsResponse delegate = null;
        MobileServiceClient client = AzureServiceAdapter.getInstance().getClient();

        /**
         *
         * @param params - contains the email of the user
         * @return the person matching the email in params
         */
        @Override
        protected Person doInBackground(TableParams... params) {
            List<Person> results = new ArrayList<>();
            String email = params[0].email;
            try {
                results = client.getTable(Person.class).where().field("EmailAddress").eq(email).execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return (results.size() > 0 ? results.get(0) : new Person());
        }

        @Override
        protected void onPostExecute(Person result){
            delegate.processFinish(result);
        }
    }

    public static class TableParams{
        String email;

        TableParams(String email){
            this.email = email;
        }
    }
}

