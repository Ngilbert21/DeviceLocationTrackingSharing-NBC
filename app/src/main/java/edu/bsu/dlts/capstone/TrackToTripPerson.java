package edu.bsu.dlts.capstone;

public class TrackToTripPerson {
    private String id;
    private String TripToPersonID;
    private String TrackID;
    private String createdAt;
    private String DeletedDateTime;

    public String getId() {
        return id;
    }

    public String getTripToPersonID() {
        return TripToPersonID;
    }

    public void setTripToPersonID(String tripToPersonID) {
        TripToPersonID = tripToPersonID;
    }

    public String getTrackID() {
        return TrackID;
    }

    public void setTrackID(String trackID) {
        TrackID = trackID;
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
