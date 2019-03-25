package edu.bsu.dlts.capstone;

public class TrackToTripPerson {
    private int TrackToTripPerson;
    private int TripToPersonID;
    private int TrackID;
    private String CreatedDateTime;
    private String DeletedDateTime;

    public int getTrackToTripPerson() {
        return TrackToTripPerson;
    }

    public void setTrackToTripPerson(int trackToTripPerson) {
        TrackToTripPerson = trackToTripPerson;
    }

    public int getTripToPersonID() {
        return TripToPersonID;
    }

    public void setTripToPersonID(int tripToPersonID) {
        TripToPersonID = tripToPersonID;
    }

    public int getTrackID() {
        return TrackID;
    }

    public void setTrackID(int trackID) {
        TrackID = trackID;
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
