package com.crilu.gothandroid.model.firestore;

import java.util.Date;

public class Tournament {

    private String id;
    private String fullName;
    private String shortName;
    private Date beginDate;
    private String location;
    private String director;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getContent() {
        return content;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public String getLocation() {
        return location;
    }

    public String getDirector() {
        return director;
    }
}
