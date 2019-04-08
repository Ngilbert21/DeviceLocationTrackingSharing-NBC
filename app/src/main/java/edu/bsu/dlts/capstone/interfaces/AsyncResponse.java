package edu.bsu.dlts.capstone.interfaces;

import com.microsoft.azure.storage.blob.ListBlobItem;

import java.util.ArrayList;
import java.util.List;

public interface AsyncResponse {
    void processFinish(List<?> output);
}

