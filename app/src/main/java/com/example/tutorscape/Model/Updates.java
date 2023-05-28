package com.example.tutorscape.Model;

public class Updates {
    private String updateID;
    private String imageUrl;
    private String updateText;
    private String updateName;

    public Updates() {
    }

    public Updates(String updateID, String imageUrl, String updateText, String updateName) {
        this.updateID = updateID;
        this.imageUrl = imageUrl;
        this.updateText = updateText;
        this.updateName = updateName;
    }

    public String getUpdateID() {
        return updateID;
    }

    public void setUpdateID(String updateID) {
        this.updateID = updateID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUpdateText() {
        return updateText;
    }

    public void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }
}
