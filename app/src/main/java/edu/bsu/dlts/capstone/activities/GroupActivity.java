package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
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
import java.util.concurrent.ExecutionException;

import edu.bsu.dlts.capstone.R;
import edu.bsu.dlts.capstone.adapters.AzureServiceAdapter;
import edu.bsu.dlts.capstone.interfaces.AsyncExistsResponse;
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
//        SharedPreferences pref = getSharedPreferences("user", 0);
//        TextView user = findViewById(R.id.editText2);
//        String userStr = pref.getString("firstName", "") + " " + pref.getString("lastName", "");
//        user.setText(userStr);
        userList = findViewById(R.id.userList);
        String groupName = getIntent().getStringExtra("groupName");
        TableParams params = new TableParams(groupName);
        AzureTableRetriever tableRetriever = new AzureTableRetriever();
        tableRetriever.delegate = this;
        tableRetriever.execute(params);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configureAddUserButton();
    }

    private void configureAddUserButton(){
        Button addUser = (Button) findViewById(R.id.button12);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent7 = new Intent(GroupActivity.this, UsersActivity.class);
                intent7.putExtra("groupName", getIntent().getStringExtra("groupName"));
                startActivity(intent7);
            }
        });
    }

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

    public static class AzureTableRetriever extends AsyncTask<TableParams, Void, List<Person>> {
        public AsyncPeopleResponse delegate = null;

        @Override
        protected List<Person> doInBackground(TableParams... params) {
            List<Person> results = new ArrayList<>();
            MobileServiceClient client = AzureServiceAdapter.getInstance().getClient();
            try {
                Group group = client.getTable(Group.class).where().field("Name").eq(params[0].groupName).execute().get().get(0);
                String groupId = group.getId();
                List<UserGroup> userGroups = client.getTable(UserGroup.class).where().field("GroupID").eq(groupId).execute().get();
                List<String> userIds = new ArrayList<>();
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
