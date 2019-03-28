package edu.bsu.dlts.capstone;

public class TripToPerson {
    private String id;
    private String PersonID;
    private String TripID;
    private String createdAt;
    private String DeletedDateTime;

    public String getId() {
        return id;
    }

    public String getPersonID() {
        return PersonID;
    }

    public void setPersonID(String personID) {
        PersonID = personID;
    }

    public String getTripID() {
        return TripID;
    }

    public void setTripID(String tripID) {
        TripID = tripID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDeletedDateTime() {
        return DeletedDateTime;
    }

    public void setDeletedDateTime(String deletedDateTime) {
        DeletedDateTime = deletedDateTime;
    }
}
