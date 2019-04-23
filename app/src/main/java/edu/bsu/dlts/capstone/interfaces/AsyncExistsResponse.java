package edu.bsu.dlts.capstone.interfaces;

import com.microsoft.azure.storage.blob.ListBlobItem;

import java.util.List;

import edu.bsu.dlts.capstone.models.Person;

public interface AsyncExistsResponse {
    void processFinish(Person output);
}
