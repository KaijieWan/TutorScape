package com.example.tutorscape.Model;

public class Favourite {
    private String TCID;
    public Favourite() {
    }

    public Favourite(String TCID) {
        this.TCID = TCID;
    }

    public String getTCID() {
        return TCID;
    }

    public void setTCID(String TCID) {
        this.TCID = TCID;
    }
}
