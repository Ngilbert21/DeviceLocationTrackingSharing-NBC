package edu.bsu.dlts.capstone;

public class Trip {
    private int TripID;
    private String TripName;
    private String StartDate;
    private String EndDate;
    private boolean TripIsPublic;
    private int OwnerPersonID;
    private String CreatedDateTime;
    private String DeletedDateTime;

    public int getTripID() {
        return TripID;
    }

    public void setTripID(int tripID) {
        TripID = tripID;
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

    public int getOwnerPersonID() {
        return OwnerPersonID;
    }

    public void setOwnerPersonID(int ownerPersonID) {
        OwnerPersonID = ownerPersonID;
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
