package edu.bsu.dlts.capstone.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.util.ArrayList;
import java.util.List;

import edu.bsu.dlts.capstone.R;
import edu.bsu.dlts.capstone.interfaces.AsyncBlobResponse;

public class PreviousToursActivity extends AppCompatActivity implements AsyncBlobResponse {

    private CloudBlockBlob activeBlob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_tours);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configureMapButton();
        AzureBlobRetriever blobRetriever = new AzureBlobRetriever();
        blobRetriever.delegate = this;
        blobRetriever.execute();
    }

    // Soon to be changed for the new formatting and matching with the recycler view
    @Override
    public void processFinish(List<ListBlobItem> output) {
        TextView tourTitle = findViewById(R.id.tourTitle);

        for (ListBlobItem blob : output){
            if (blob.getClass() == CloudBlockBlob.class){
                activeBlob = (CloudBlockBlob) blob;
                String path = blob.getUri().getPath();
                String name = path.substring(path.lastIndexOf('/') + 1);
                Log.d("BLOBTEST", name);
                tourTitle.setText(name);
            }
        }
    }

    private void configureMapButton(){
        Button endTour = findViewById(R.id.endTour);
        endTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMaps = new Intent(PreviousToursActivity.this, MapsActivity.class);
//                try {
//                    toMaps.putExtra("geojson", activeBlob.downloadText());
//                } catch (StorageException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                startActivity(toMaps);
            }
        });
    }

    public static class AzureBlobRetriever extends AsyncTask<Void, Void, ArrayList<ListBlobItem>> {
        private static final String storageURL = "http://brstrayer.blob.core.windows.net";
        private static final String storageContainer = "geojson";
        private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=brstrayer;AccountKey=9RqBtAtKXwGsDInFfz2uECfwe3VBymaRriMG9ms7gLjfKJ9ku0uG3MZX7P855AdcOwU72WTnei8q2FMlwcB1MA==;EndpointSuffix=core.windows.net";
        public AsyncBlobResponse delegate = null;

        @Override
        protected ArrayList<ListBlobItem> doInBackground(Void... params) {
            ArrayList<ListBlobItem> blobs = new ArrayList<>();
            try{
                CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
                for (ListBlobItem item : container.listBlobs()) {

                    blobs.add(item);
                }
    //            foreach (IListBlobItem item in container.ListBlobs(null, false))
    //            {
    //                if (item.GetType() == typeof(CloudBlockBlob))
    //                {
    //                    CloudBlockBlob blob = (CloudBlockBlob)item;
    //                    Console.WriteLine("Block blob of length {0}: {1}", blob.Properties.Length, blob.Uri);
    //                }
    //                else if (item.GetType() == typeof(CloudPageBlob))
    //                {
    //                    CloudPageBlob pageBlob = (CloudPageBlob)item;
    //                    Console.WriteLine("Page blob of length {0}: {1}", pageBlob.Properties.Length, pageBlob.Uri);
    //                }
    //                else if (item.GetType() == typeof(CloudBlobDirectory))
    //                {
    //                    CloudBlobDirectory directory = (CloudBlobDirectory)item;
    //                    Console.WriteLine("Directory: {0}", directory.Uri);
    //                }
    //            }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return blobs;
        }

        @Override
        protected void onPostExecute(ArrayList<ListBlobItem> result){
            delegate.processFinish(result);
        }
    }


}

