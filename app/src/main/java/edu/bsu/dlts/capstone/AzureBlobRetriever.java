package edu.bsu.dlts.capstone;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.util.ArrayList;

public class AzureBlobRetriever extends AsyncTask<Void, Void, ArrayList<ListBlobItem>> {
    private static final String storageURL = "http://brstrayer.blob.core.windows.net";
    private static final String storageContainer = "geojson";
    private static final String storageConnectionString = "CONNECTIONSTRING";
    public AsyncResponse delegate = null;

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
