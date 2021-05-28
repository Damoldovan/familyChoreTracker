package com.example.familychoretracker;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

public class Chore {

    public String choreAssignedTo, choreAssignedBy, choreDescription, userUid, choreQuestion, choreID;
    public String choreComplete, choreInProgress, choreNotStarted, choreDueDate, choreAssignedDate;


    public String getChoreID() {return choreID;}

    public void setChoreID(String choreID){
        this.choreID = choreID;
    }

    public String getChoreAssignedTo() {
        return choreAssignedTo;
    }

    public void setChoreAssignedTo(String choreAssignedTo) {
        this.choreAssignedTo = choreAssignedTo;
    }

    public String getChoreAssignedBy() {
        return choreAssignedBy;
    }

    public void setChoreAssignedBy(String choreAssignedBy) {
        this.choreAssignedBy = choreAssignedBy;
    }

    public String getChoreDescription() {
        return choreDescription;
    }

    public void setChoreDescription(String choreDescription) {
        this.choreDescription = choreDescription;
    }

    public String getUserUid() {
        return userUid;
    }


    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getChoreComplete() {
        return choreComplete;
    }

    public void setChoreComplete(String choreComplete) {
        this.choreComplete = choreComplete;
    }

    public String getChoreInProgress() {
        return choreInProgress;
    }

    public void setChoreInProgress(String choreInProgress) {
        this.choreInProgress = choreInProgress;
    }

    public String getChoreNotStarted() {
        return choreNotStarted;
    }

    public void setChoreNotStarted(String choreNotStarted) {
        this.choreNotStarted = choreNotStarted;
    }

    public String getChoreQuestion() {
        return choreQuestion;
    }

    public void setChoreQuestion(String choreQuestion) {
        this.choreQuestion = choreQuestion;
    }

    public String getChoreAssignedDate() {
        return choreAssignedDate;
    }

    public void setChoreAssignedDate(String choreAssignedDate) {
        this.choreAssignedDate = choreAssignedDate;
    }
    public String getChoreDueDate() {
        return choreDueDate;
    }

    public void setChoreDueDate(String choreDueDate) {
        this.choreDueDate = choreDueDate;
    }

}
