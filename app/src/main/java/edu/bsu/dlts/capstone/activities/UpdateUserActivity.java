package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.bsu.dlts.capstone.adapters.AzureServiceAdapter;
import edu.bsu.dlts.capstone.interfaces.AsyncExistsResponse;
import edu.bsu.dlts.capstone.interfaces.AsyncPersonResponse;
import edu.bsu.dlts.capstone.models.Person;

public class UpdateUserActivity extends UserActivity implements AsyncPersonResponse {
    AzureTableRetriever tableRetriever;
    SharedPreferences pref;
    String email;
    String firstNameStr;
    String lastNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        tableRetriever = new AzureTableRetriever();
        tableRetriever.delegate = this;
        pref = getApplicationContext().getSharedPreferences("user", 0);
        email = pref.getString("email", "");
        firstNameStr = pref.getString("firstName", "");
        lastNameStr = pref.getString("lastName", "");

        firstName.setText(firstNameStr);
        lastName.setText(lastNameStr);
    }

    @Override
    public void onClick() {

        UpdateUserActivity.TableParams params = new UpdateUserActivity.TableParams(email);

        tableRetriever.execute(params);
    }

    @Override
    public void processFinish(Person output) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", output.getMId());
        editor.apply();

        if(!firstName.getText().toString().equals(firstNameStr)){
            output.setFirstName(firstName.getText().toString());
            editor.putString("firstName", firstName.getText().toString());
            editor.apply();
        }

        if(!lastName.getText().toString().equals(lastNameStr)){
            output.setLastName(lastName.getText().toString());
            editor.putString("lastName", lastName.getText().toString());
            editor.apply();
        }

        AzureServiceAdapter.getInstance().getClient().getTable(Person.class).update(output);

        Intent i = new Intent(UpdateUserActivity.this, MainMenuActivity.class);
        startActivity(i);
    }

    public static class AzureTableRetriever extends AsyncTask<UpdateUserActivity.TableParams, Void, Person> {
        public AsyncPersonResponse delegate = null;

        @Override
        protected Person doInBackground(UpdateUserActivity.TableParams... params) {
            List<Person> results = new ArrayList<>();
            try {
                results = AzureServiceAdapter.getInstance().getClient().getTable(Person.class)
                        .where()
                        .field("EmailAddress").eq(params[0].email)
                        .execute()
                        .get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return (results.get(0));
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
