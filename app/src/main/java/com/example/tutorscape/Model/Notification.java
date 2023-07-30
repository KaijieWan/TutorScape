package com.example.tutorscape.Model;

import com.google.firebase.database.PropertyName;

public class Notification {
    private boolean latestUpdates;
    private boolean favReminder;
    private String numHours;
    private boolean favCount;
    private boolean messageCount;

    public Notification() {
    }

    public Notification(boolean latestUpdates, boolean favReminder, String numHours, boolean favCount, boolean messageCount) {
        this.latestUpdates = latestUpdates;
        this.favReminder = favReminder;
        this.numHours = numHours;
        this.favCount = favCount;
        this.messageCount = messageCount;
    }

    public boolean isLatestUpdates() {
        return latestUpdates;
    }

    public void setLatestUpdates(boolean latestUpdates) {
        this.latestUpdates = latestUpdates;
    }

    public boolean isFavReminder() {
        return favReminder;
    }

    public void setFavReminder(boolean favReminder) {
        this.favReminder = favReminder;
    }

    public String getNumHours() {
        return numHours;
    }

    public void setNumHours(String numHours) {
        this.numHours = numHours;
    }

    @PropertyName("favCount")
    public boolean isFavCount() {
        return favCount;
    }

    public void setFavCount(boolean favCount) {
        this.favCount = favCount;
    }

    @PropertyName("messageCount")
    public boolean isMessageCount() {
        return messageCount;
    }

    public void setMessageCount(boolean messageCount) {
        this.messageCount = messageCount;
    }
}
