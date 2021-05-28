package com.example.familychoretracker;


public class User {

    private String fullName;
    private String email;
    private String primaryUserId;
    private String isAdmin;
    private String myUserId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrimaryUserId() {
        return primaryUserId;
    }

    public void setPrimaryUserId(String primaryUserId) {
        this.primaryUserId = primaryUserId;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getMyUserId() {
        return myUserId;
    }

    public void setMyUserId(String myUserId) {
        this.myUserId = myUserId;
    }
}



