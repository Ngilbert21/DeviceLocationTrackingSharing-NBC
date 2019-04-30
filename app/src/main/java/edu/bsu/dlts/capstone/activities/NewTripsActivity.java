package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.bsu.dlts.capstone.R;
import edu.bsu.dlts.capstone.adapters.AzureServiceAdapter;
import edu.bsu.dlts.capstone.interfaces.AsyncTripsResponse;
import edu.bsu.dlts.capstone.models.Trip;
import edu.bsu.dlts.capstone.models.TripToPerson;

/**
 * This Activity is used to create a new trip
 */
public class NewTripsActivity extends AppCompatActivity implements AsyncTripsResponse{
    public EditText name;
    public EditText startTime;
    public EditText endTime;
    public Button createTrip;
    public AsyncTripsResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        response = this;
        name = findViewById(R.id.name);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        createTrip = findViewById(R.id.createTrip);

        createTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validates input
                if( TextUtils.isEmpty(name.getText())){
                    name.setError( "Trip name is required!" );
                }else if( TextUtils.isEmpty(startTime.getText())){
                    startTime.setError( "Start time is required!" );
                }else if( TextUtils.isEmpty(endTime.getText())){
                    endTime.setError( "End time is required!" );
                }
                else{
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("user", 0);
                    SharedPreferences tripPref = getApplicationContext().getSharedPreferences("trip", 0);
                    SharedPreferences.Editor editor = tripPref.edit();
                    editor.putString("name", name.getText().toString());
                    editor.apply();

                    Trip trip = new Trip();
                    trip.setTripName(name.getText().toString());
                    trip.setStartDate(startTime.getText().toString());
                    trip.setEndDate(endTime.getText().toString());
                    trip.setOwnerPersonID(pref.getString("id", ""));

                    MobileServiceClient client = AzureServiceAdapter.getInstance().getClient();

                    client.getTable(Trip.class).insert(trip);

                    TableParams params = new TableParams(pref.getString("id", ""));
                    AzureTableRetriever tableRetriever = new AzureTableRetriever();
                    tableRetriever.delegate = response;
                    tableRetriever.execute(params);

                    Intent toTour = new Intent(NewTripsActivity.this, TourActivity.class);
                    startActivity(toTour);
                }
            }
        });
    }

    /**
     * Adds user to current trip
     * @param output - the new trip
     */
    @Override
    public void processFinish(List<Trip> output) {
        Trip trip = output.get(0);
        TripToPerson tripToPerson = new TripToPerson();
        tripToPerson.setPersonID(trip.getOwnerPersonID());
        tripToPerson.setTripID(trip.getId());
        AzureServiceAdapter.getInstance().getClient().getTable(TripToPerson.class).insert(tripToPerson);
    }

    /**
     * Retrieves the newly created trip
     */
    public static class AzureTableRetriever extends AsyncTask<TableParams, Void, List<Trip>> {
        AsyncTripsResponse delegate = null;

        @Override
        protected List<Trip> doInBackground(TableParams... params) {
            List<Trip> results = new ArrayList<>();
            MobileServiceClient client = AzureServiceAdapter.getInstance().getClient();

            try {
                String userId = params[0].userId;
                results = client.getTable(Trip.class).where().field("OwnerPersonID").eq(userId).orderBy("createdAt", QueryOrder.Descending).execute().get();
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

    static class TableParams{
        String userId;

        TableParams(String userId){
            this.userId = userId;
        }
    }
}
