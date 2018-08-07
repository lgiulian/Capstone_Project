package com.crilu.gothandroid.model.firestore;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Tournament {

    public static final String FULL_NAME = "fullName";
    public static final String SHORT_NAME = "shortName";
    public static final String CONTENT = "content";
    public static final String BEGIN_DATE = "beginDate";
    public static final String END_DATE = "endDate";
    public static final String LOCATION = "location";
    public static final String DIRECTOR = "director";
    public static final String RESULT_CONTENT = "content";
    public static final String CREATOR = "creator";
    public static final String CREATION_DATE = "creationDate";
    public static final String LAST_MODIFICATION_DATE = "lastModificationDate";

    private Long id;
    private String identity;
    private String fullName;
    private String shortName;
    private Date beginDate;
    private Date endDate;
    private String location;
    private String director;
    private String content;
    private String creator;
    private Date creationDate;
    private Date lastModificationDate;

    public Tournament() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", identity='" + identity + '\'' +
                ", fullName='" + fullName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", location='" + location + '\'' +
                ", director='" + director + '\'' +
                ", content='" + content + '\'' +
                ", creator='" + creator + '\'' +
                ", creationDate=" + creationDate +
                ", lastModificationDate=" + lastModificationDate +
                '}';
    }
}
