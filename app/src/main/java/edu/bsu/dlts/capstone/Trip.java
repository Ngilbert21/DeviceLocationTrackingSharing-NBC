package edu.bsu.dlts.capstone;

public class Trip {
    private String id;
    private String TripName;
    private String StartDate;
    private String EndDate;
    private boolean TripIsPublic;
    private String OwnerPersonID;
    private String createdAt;
    private String DeletedDateTime;

    public String getId() {
        return id;
    }

    public String getTripName() {
        return TripName;
    }

    public void setTripName(String tripName) {
        TripName = tripName;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public boolean isTripIsPublic() {
        return TripIsPublic;
    }

    public void setTripIsPublic(boolean tripIsPublic) {
        TripIsPublic = tripIsPublic;
    }

    public String getOwnerPersonID() {
        return OwnerPersonID;
    }

    public void setOwnerPersonID(String ownerPersonID) {
        OwnerPersonID = ownerPersonID;
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
