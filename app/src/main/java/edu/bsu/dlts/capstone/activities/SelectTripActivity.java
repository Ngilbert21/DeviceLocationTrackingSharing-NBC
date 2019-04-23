package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import edu.bsu.dlts.capstone.interfaces.AsyncTripsResponse;
import edu.bsu.dlts.capstone.models.Person;
import edu.bsu.dlts.capstone.models.Trip;
import edu.bsu.dlts.capstone.models.TripToPerson;

public class SelectTripActivity extends AppCompatActivity implements AsyncTripsResponse {

    LinearLayout tripList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_trip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences pref = getSharedPreferences("user", 0);
        TableParams tableParams = new TableParams(pref.getString("id", ""));
        AzureTableRetriever tableRetriever = new AzureTableRetriever();
        tableRetriever.delegate = this;
        tableRetriever.execute(tableParams);

        tripList = findViewById(R.id.tripList);
    }

    @Override
    public void processFinish(List<Trip> output) {
        LinearLayout parent = findViewById(R.id.tripList);
        LinearLayout item = new LinearLayout(this);
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemParams.weight = 1;
        item.setLayoutParams(itemParams);
        item.setOrientation(LinearLayout.HORIZONTAL);
        for (Trip trip :
                output) {
            Log.d("SelectTripActivity", trip.getTripName());
            LinearLayout.LayoutParams inlineparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            inlineparams.weight = 1;
            TextView tv=new TextView(this);
            tv.setLayoutParams(inlineparams);
            String name = trip.getTripName();
            tv.setText(name);
            item.addView(tv);
            Button viewTrip = new Button(this);
            viewTrip.setLayoutParams(inlineparams);
            viewTrip.setText("Select");
            viewTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toSelect = new Intent(SelectTripActivity.this, TourActivity.class);
                    startActivity(toSelect);
                }
            });
            item.addView(viewTrip);
            parent.addView(item);
        }
    }

    public static class AzureTableRetriever extends AsyncTask<TableParams, Void, List<Trip>> {
        public AsyncTripsResponse delegate = null;

        @Override
        protected List<Trip> doInBackground(TableParams... params) {
            List<Trip> results = new ArrayList<>();
            MobileServiceClient client = AzureServiceAdapter.getInstance().getClient();
            try {
                String userId = params[0].userId;
                Log.d("SelectTripActivity", userId);
                List<TripToPerson> userTrips = client.getTable(TripToPerson.class).where().field("PersonID").eq(userId).execute().get();
                List<String> tripIds = new ArrayList<>();
                for (TripToPerson userTrip :
                        userTrips) {
                    tripIds.add(userTrip.getTripID());
                }
                for (String id :
                        tripIds) {
                    results.add(client.getTable(Trip.class).lookUp(id).get());
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<Trip> result){
            delegate.processFinish(result);
        }
    }

    public static class TableParams{
        String userId;

        TableParams(String userId){
            this.userId = userId;
        }
    }

}
