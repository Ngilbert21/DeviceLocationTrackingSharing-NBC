package edu.bsu.dlts.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.IOException;
import java.util.ArrayList;

public class PreviousTours extends AppCompatActivity implements AsyncResponse{

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
    public void processFinish(ArrayList<ListBlobItem> output) {
        TextView tourTitle = findViewById(R.id.editText15);

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
        Button endTour = (Button) findViewById(R.id.button8);
        endTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(PreviousTours.this, MapsActivity.class);
//                try {
//                    intent2.putExtra("geojson", activeBlob.downloadText());
//                } catch (StorageException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                startActivity(intent2);
            }
        });
    }
}
