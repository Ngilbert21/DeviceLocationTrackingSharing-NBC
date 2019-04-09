package edu.bsu.dlts.capstone.interfaces;

import com.microsoft.azure.storage.blob.ListBlobItem;

import java.util.List;

public interface AsyncBlobResponse {
    void processFinish(List<ListBlobItem> output);
}
