package com.crilu.gothandroid.model.firestore;

import java.util.Calendar;
import java.util.Date;

public class Tournament {

    public static final String FULL_NAME = "fullName";
    public static final String SHORT_NAME = "shortName";
    public static final String CONTENT = "content";
    public static final String BEGIN_DATE = "beginDate";
    public static final String LOCATION = "location";
    public static final String DIRECTOR = "director";
    public static final String RESULT_CONTENT = "content";
    public static final String CREATOR = "creator";
    public static final String CREATION_DATE = "creationDate";

    private Integer id;
    private String identity;
    private String fullName;
    private String shortName;
    private Date beginDate;
    private String location;
    private String director;
    private String content;
    private String creator;
    private Date creationDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
