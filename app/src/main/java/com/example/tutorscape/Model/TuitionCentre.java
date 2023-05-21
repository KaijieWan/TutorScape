package com.example.tutorscape.Model;

import java.util.ArrayList;

public class TuitionCentre {
    private String id;
    private String name;
    private String levels;
    private String exams;
    private String subjects;
    //Refers to tuition format: could have multiple (1-to-1, online, group...)
    private String type;
    private String postal;
    private String address;
    private String contactNo;
    private String email;
    private String operatingHrs;
    private String imageUrl;
    private String website;
    private String rating_num;

    public TuitionCentre() {
    }

    public TuitionCentre(String id, String name, String levels, String exams, String subjects, String type,
                         String postal, String address, String contactNo, String email, String operatingHrs, String imageUrl, String website, String rating_num) {
        this.id = id;
        this.name = name;
        this.levels = levels;
        this.exams = exams;
        this.subjects = subjects;
        this.type = type;
        this.postal = postal;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.operatingHrs = operatingHrs;
        this.imageUrl = imageUrl;
        this.website = website;
        this.rating_num = rating_num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
    }

    public String getExams() {
        return exams;
    }

    public void setExams(String exams) {
        this.exams = exams;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getOperatingHrs() {
        return operatingHrs;
    }

    public void setOperatingHrs(String operatingHrs) {
        this.operatingHrs = operatingHrs;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getRating_num() {
        return rating_num;
    }

    public void setRating_num(String rating_num) {
        this.rating_num = rating_num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
