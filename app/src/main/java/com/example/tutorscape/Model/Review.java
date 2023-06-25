package com.example.tutorscape.Model;

import java.io.Serializable;

public class Review implements Serializable{
    private String id;
    private String UID;
    private String TCID;
    private String review_date;
    private String subjects_enrolled;
    private String review_text;
    private String rating_num;
    private boolean isEdited;

    public Review() {
    }

    public Review(String id, String UID, String TCID, String review_date, String subjects_enrolled, String review_text, String rating_num, boolean isEdited) {
        this.id = id;
        this.UID = UID;
        this.TCID = TCID;
        this.review_date = review_date;
        this.subjects_enrolled = subjects_enrolled;
        this.review_text = review_text;
        this.rating_num = rating_num;
        this.isEdited = isEdited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getTCID() {
        return TCID;
    }

    public void setTCID(String TCID) {
        this.TCID = TCID;
    }

    public String getReview_date() {
        return review_date;
    }

    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }

    public String getSubjects_enrolled() {
        return subjects_enrolled;
    }

    public void setSubjects_enrolled(String subjects_enrolled) {
        this.subjects_enrolled = subjects_enrolled;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public String getRating_num() {
        return rating_num;
    }

    public void setRating_num(String rating_num) {
        this.rating_num = rating_num;
    }

    public boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }
}
