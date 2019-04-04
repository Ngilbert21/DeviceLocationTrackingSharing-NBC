package edu.bsu.dlts.capstone;

public class Group {
    private String id;
    private String Name;
    private String createdAt;
    private boolean IsActive;

    public String getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

    public Group(String Name){
        this.setName(Name);

    }
}
