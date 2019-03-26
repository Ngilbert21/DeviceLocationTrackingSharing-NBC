package edu.bsu.dlts.capstone;

import com.microsoft.azure.storage.blob.ListBlobItem;

import java.util.ArrayList;

public interface AsyncResponse {
    void processFinish(ArrayList<ListBlobItem> output);
}

