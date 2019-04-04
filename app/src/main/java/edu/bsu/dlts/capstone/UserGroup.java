package edu.bsu.dlts.capstone;

public class UserGroup {
    private String id;
    private String UserID;
    private String GroupID;
    private String createdAt;
    private boolean IsActive;

    public String getId() {
        return id;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
