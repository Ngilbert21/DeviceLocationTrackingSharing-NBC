package edu.bsu.dlts.capstone;

import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;

public class Person {
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("PersonID")
    private int PersonID;

    @com.google.gson.annotations.SerializedName("FirstName")
    private String FirstName;

    @com.google.gson.annotations.SerializedName("LastName")
    private String LastName;

    @com.google.gson.annotations.SerializedName("EmailAddress")
    private String EmailAddress;

    @com.google.gson.annotations.SerializedName("CreatedDateTime")
    private DateTimeOffset CreatedDateTime;

    @com.google.gson.annotations.SerializedName("DeletedDateTime")
    private DateTimeOffset DeletedDateTime;


    public int getPersonID() {
        return PersonID;
    }

    public void setPersonID(int personID) {
        PersonID = personID;
    }

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

    public DateTimeOffset getCreatedDateTime() {
        return CreatedDateTime;
    }

    public void setCreatedDateTime(DateTimeOffset createdDateTime) {
        CreatedDateTime = createdDateTime;
    }

    public DateTimeOffset getDeletedDateTime() {
        return DeletedDateTime;
    }

    public void setDeletedDateTime(DateTimeOffset deletedDateTime) {
        DeletedDateTime = deletedDateTime;
    }

    public Person(String firstName){
        this.setFirstName(firstName);

    }


    @Override
    public String toString() {
        return getEmailAddress();
    }

}
