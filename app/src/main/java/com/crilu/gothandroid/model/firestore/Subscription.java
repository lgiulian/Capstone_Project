package com.crilu.gothandroid.model.firestore;

import java.util.Date;

public class Subscription {

    public static final String INTENT_PARTICIPANT = "participant";
    public static final String INTENT_OBSERVER = "observer";

    public static final String STATE_ACTIVE = "active";

    public static final String UID = "uid";
    public static final String EGF_PIN = "egfPin";
    public static final String FFG_LIC = "ffgLic";
    public static final String AGA_ID = "agaId";
    public static final String INTENT = "intent"; // participant, observer
    public static final String SUBSCRIPTION_DATE = "subscriptionDate";
    public static final String STATE = "state"; // active, inactive

    public Subscription() {
    }

    public Subscription(Long tournamentId, String uid, String egfPin, String ffgLic, String agaId, String intent, Date subscriptionDate, String state) {
        this.tournamentId = tournamentId;
        this.uid = uid;
        this.egfPin = egfPin;
        this.ffgLic = ffgLic;
        this.agaId = agaId;
        this.intent = intent;
        this.subscriptionDate = subscriptionDate;
        this.state = state;
    }

    private Long id;
    private String identity;
    private Long tournamentId;
    private String tournamentIdentity;
    private String uid;
    private String egfPin;
    private String ffgLic;
    private String agaId;
    private String intent;
    private Date subscriptionDate;
    private String state;

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

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentIdentity() {
        return tournamentIdentity;
    }

    public void setTournamentIdentity(String tournamentIdentity) {
        this.tournamentIdentity = tournamentIdentity;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEgfPin() {
        return egfPin;
    }

    public void setEgfPin(String egfPin) {
        this.egfPin = egfPin;
    }

    public String getFfgLic() {
        return ffgLic;
    }

    public void setFfgLic(String ffgLic) {
        this.ffgLic = ffgLic;
    }

    public String getAgaId() {
        return agaId;
    }

    public void setAgaId(String agaId) {
        this.agaId = agaId;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
