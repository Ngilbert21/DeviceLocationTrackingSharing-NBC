package edu.bsu.dlts.capstone.adapters;

import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import edu.bsu.dlts.capstone.models.TrackToTripPerson;

public class AzureBlobAdapter extends AsyncTask<BlobParams, Void, Void> {
    private static final String storageURL = "http://brstrayer.blob.core.windows.net";
    private static final String storageContainer = "geojson";
    private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=brstrayer;AccountKey=9RqBtAtKXwGsDInFfz2uECfwe3VBymaRriMG9ms7gLjfKJ9ku0uG3MZX7P855AdcOwU72WTnei8q2FMlwcB1MA==;EndpointSuffix=core.windows.net";

    @Override
    protected Void doInBackground(BlobParams... params) {
        try{
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
            CloudBlockBlob blob = container.getBlockBlobReference("blobTest");
            blob.uploadText(params[0].geojson);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

//TODO: move to separate class
class BlobParams{
    TrackToTripPerson user;
    String geojson;

    BlobParams(TrackToTripPerson user, String geojson){
        this.user = user;
        this.geojson = geojson;
    }
}
