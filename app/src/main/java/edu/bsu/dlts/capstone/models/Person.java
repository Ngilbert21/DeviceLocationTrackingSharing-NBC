package edu.bsu.dlts.capstone.models;

public class Person {
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("FirstName")
    private String FirstName;

    @com.google.gson.annotations.SerializedName("LastName")
    private String LastName;

    @com.google.gson.annotations.SerializedName("EmailAddress")
    private String EmailAddress;

    @com.google.gson.annotations.SerializedName("createdAt")
    private String CreatedDateTime;

    @com.google.gson.annotations.SerializedName("DeletedDateTime")
    private String DeletedDateTime;

    public String getMId() {return mId;}

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
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
