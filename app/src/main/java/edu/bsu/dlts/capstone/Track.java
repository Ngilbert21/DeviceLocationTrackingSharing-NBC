package edu.bsu.dlts.capstone;

public class Track {
    private int TrackID;
    private String PathName;
    private String StartTime;
    private String EndTime;
    private String CreatedDateTime;
    private String DeletedDateTime;

    public int getTrackID() {
        return TrackID;
    }

    public void setTrackID(int trackID) {
        TrackID = trackID;
    }

    public String getPathName() {
        return PathName;
    }

    public void setPathName(String pathName) {
        PathName = pathName;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
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
