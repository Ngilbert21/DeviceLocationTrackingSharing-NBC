package edu.bsu.dlts.capstone.interfaces;

import java.util.List;

import edu.bsu.dlts.capstone.models.Person;

public interface AsyncPeopleResponse {
    void processFinish(List<Person> output);
}
