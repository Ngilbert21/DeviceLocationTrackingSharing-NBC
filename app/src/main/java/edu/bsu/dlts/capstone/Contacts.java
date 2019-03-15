package edu.bsu.dlts.capstone;

import com.google.android.gms.common.api.GoogleApiClient;

public class Contacts {
    public String name;

    public Contacts(){

    }

    public Contacts(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
