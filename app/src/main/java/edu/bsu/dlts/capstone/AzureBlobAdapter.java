package edu.bsu.dlts.capstone;

import android.os.AsyncTask;

import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class AzureBlobAdapter extends AsyncTask<BlobParams, Void, Void> {
    private static final String storageURL = "http://brstrayer.blob.core.windows.net";
    private static final String storageContainer = "geojson";
    private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=brstrayer;AccountKey=ezwwhzl+ilhvuzWt18DaBv/dbCfJ4C6Ix2yo6vN1XguzfSMMD+75NHFsZ54ugDZej4aShXYPi1Kp7vAdLLRa5g==;EndpointSuffix=core.windows.net";

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

class BlobParams{
    TrackToTripPerson user;
    String geojson;

    BlobParams(TrackToTripPerson user, String geojson){
        this.user = user;
        this.geojson = geojson;
    }
}
