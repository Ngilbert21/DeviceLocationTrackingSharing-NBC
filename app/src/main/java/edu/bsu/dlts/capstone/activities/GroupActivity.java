package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import edu.bsu.dlts.capstone.R;
import edu.bsu.dlts.capstone.adapters.AzureServiceAdapter;
import edu.bsu.dlts.capstone.interfaces.AsyncPeopleResponse;
import edu.bsu.dlts.capstone.models.Group;
import edu.bsu.dlts.capstone.models.Person;
import edu.bsu.dlts.capstone.models.UserGroup;

public class GroupActivity extends AppCompatActivity implements AsyncPeopleResponse {
    LinearLayout userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        userList = findViewById(R.id.userList);

        // Get all users in the group
        String groupName = getIntent().getStringExtra("groupName");
        TableParams params = new TableParams(groupName);
        AzureTableRetriever tableRetriever = new AzureTableRetriever();
        tableRetriever.delegate = this;
        tableRetriever.execute(params);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        configureAddUserButton();
    }

    /**
     * Sets button to link to UsersActivity
     */
    private void configureAddUserButton(){
        Button addUser = findViewById(R.id.addUser);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUsers = new Intent(GroupActivity.this, UsersActivity.class);
                toUsers.putExtra("groupName", getIntent().getStringExtra("groupName"));
                startActivity(toUsers);
            }
        });
    }

    /**
     * Updates user list after Azure call
     * @param output - list of users in the group
     */
    @Override
    public void processFinish(List<Person> output) {
        for (Person person :
                output) {
            ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView tv=new TextView(this);
            tv.setLayoutParams(lparams);
            String name = person.getFirstName() + " " + person.getLastName();
            tv.setText(name);
            userList.addView(tv);
        }
    }

    /**
     * Retrieves every user in the specified group
     */
    public static class AzureTableRetriever extends AsyncTask<TableParams, Void, List<Person>> {
        public AsyncPeopleResponse delegate = null;

        /**
         *
         * @param params - contains the name of the specified group
         * @return list of users in the group
         */
        @Override
        protected List<Person> doInBackground(TableParams... params) {
            List<Person> results = new ArrayList<>();

            // Get the current Azure client
            MobileServiceClient client = AzureServiceAdapter.getInstance().getClient();
            try {
                // Find the group associated with the name in params
                Group group = client.getTable(Group.class).where().field("Name").eq(params[0].groupName).execute().get().get(0);
                String groupId = group.getId();

                // Find all users in the group
                List<UserGroup> userGroups = client.getTable(UserGroup.class).where().field("GroupID").eq(groupId).execute().get();
                List<String> userIds = new ArrayList<>();

                // Add users to results list
                for (UserGroup userGroup :
                        userGroups) {
                    userIds.add(userGroup.getUserID());
                }
                for (String id :
                        userIds) {
                    results.add(client.getTable(Person.class).lookUp(id).get());
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<Person> result){
            delegate.processFinish(result);
        }
    }

    public static class TableParams{
        String groupName;

        TableParams(String groupName){
            this.groupName = groupName;
        }
    }

}
