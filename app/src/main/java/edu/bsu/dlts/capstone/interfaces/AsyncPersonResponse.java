package edu.bsu.dlts.capstone.interfaces;

import edu.bsu.dlts.capstone.models.Person;

public interface AsyncPersonResponse {
    void processFinish(Person output);
}
