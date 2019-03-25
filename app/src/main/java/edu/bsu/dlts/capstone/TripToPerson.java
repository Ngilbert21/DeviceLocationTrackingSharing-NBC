package edu.bsu.dlts.capstone;

public class TripToPerson {
    private int TripToPersonID;
    private int PersonID;
    private int TripID;
    private String CreatedDateTime;
    private String DeletedDateTime;

    public int getTripToPersonID() {
        return TripToPersonID;
    }

    public void setTripToPersonID(int tripToPersonID) {
        TripToPersonID = tripToPersonID;
    }

    public int getPersonID() {
        return PersonID;
    }

    public void setPersonID(int personID) {
        PersonID = personID;
    }

    public int getTripID() {
        return TripID;
    }

    public void setTripID(int tripID) {
        TripID = tripID;
    }

    public String getCreatedDateTime() {
        return CreatedDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        CreatedDateTime = createdDateTime;
    }

    public String getDeletedDateTime() {
        return DeletedDateTime;
    }

    public void setDeletedDateTime(String deletedDateTime) {
        DeletedDateTime = deletedDateTime;
    }
}
