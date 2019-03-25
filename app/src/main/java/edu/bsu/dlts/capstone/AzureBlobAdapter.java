package edu.bsu.dlts.capstone;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.util.Calendar;

public class AzureBlobAdapter {
    private static final String storageURL = "http://brstrayer.blob.core.windows.net";
    private static final String storageContainer = "geojson";
    private static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=brstrayer;AccountKey=ezwwhzl+ilhvuzWt18DaBv/dbCfJ4C6Ix2yo6vN1XguzfSMMD+75NHFsZ54ugDZej4aShXYPi1Kp7vAdLLRa5g==;EndpointSuffix=core.windows.net";

    protected void storeGeoJSONInBlobStorage(String user, String geojson){
        try{
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(storageContainer);
            String timestamp = Calendar.getInstance().getTime().toString();
            CloudBlockBlob blob = container.getBlockBlobReference(user + " at " + timestamp);
            blob.uploadText(geojson);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
