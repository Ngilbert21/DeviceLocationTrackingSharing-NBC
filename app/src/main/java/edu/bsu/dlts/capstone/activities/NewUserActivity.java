package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import edu.bsu.dlts.capstone.adapters.AzureServiceAdapter;
import edu.bsu.dlts.capstone.models.Person;

public class NewUserActivity extends UserActivity {

    @Override
    public void onClick() {
        if( TextUtils.isEmpty(firstName.getText())){

            firstName.setError( "First name is required!" );

        }else if( TextUtils.isEmpty(lastName.getText())){
            lastName.setError( "Last name is required!" );
        }else{
            SharedPreferences pref = getApplicationContext().getSharedPreferences("user", 0);
            SharedPreferences.Editor editor = pref.edit();

            String email = pref.getString("email", "");
            String firstNameStr = firstName.getText().toString();
            String lastNameStr = lastName.getText().toString();

            editor.putString("firstName", firstNameStr);
            editor.putString("lastName", lastNameStr);
            editor.apply();

            Person person = new Person();
            person.setFirstName(firstNameStr);
            person.setLastName(lastNameStr);
            person.setEmailAddress(email);
            MobileServiceClient client = AzureServiceAdapter.getInstance().getClient();
            Log.d("CREATE USER", "Creating user...");
            client.getTable(Person.class).insert(person);

            Intent i = new Intent(NewUserActivity.this, MainMenuActivity.class);
            startActivity(i);
        }
    }
}
