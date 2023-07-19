package com.example.tutorscape.Model;

public class Message {
    private String UID;
    private String date;
    private String title;
    private String content;
    private boolean isRead;
    private String messageID;

    public Message() {
    }

    public Message(String UID, String date, String title, String content, boolean isRead, String messageID) {
        this.UID = UID;
        this.date = date;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.messageID = messageID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
