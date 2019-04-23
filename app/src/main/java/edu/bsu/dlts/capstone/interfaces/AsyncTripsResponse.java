package edu.bsu.dlts.capstone.interfaces;

import java.util.List;

import edu.bsu.dlts.capstone.models.Trip;

public interface AsyncTripsResponse {
    void processFinish(List<Trip> output);
}