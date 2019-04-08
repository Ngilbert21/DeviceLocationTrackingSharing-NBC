package edu.bsu.dlts.capstone.adapters;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.bsu.dlts.capstone.interfaces.AsyncResponse;
import edu.bsu.dlts.capstone.models.Person;

public class AzureTableRetriever extends AsyncTask<Void, Void, List<Person>> {
    public AsyncResponse delegate = null;

    @Override
    protected List<Person> doInBackground(Void... voids) {
        List<Person> results = new ArrayList<>();
        try {
            results = AzureServiceAdapter.getInstance().getClient().getTable(Person.class)
                    .where()
                    .execute()
                    .get();
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
