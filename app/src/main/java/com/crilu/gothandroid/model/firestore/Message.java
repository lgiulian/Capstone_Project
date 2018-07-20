package com.crilu.gothandroid.model.firestore;

import java.util.Date;

public class Message {

    public static final String TOKEN = "token";
    public static final String MESSAGE = "message";
    public static final String FROM = "from";
    public static final String MESSAGE_DATE = "messageDate";

    private Long id;
    private String tournamentIdentity;
    private String title;
    private String message;
    private String command;
    private String egfPin;
    private Date messageDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTournamentIdentity() {
        return tournamentIdentity;
    }

    public void setTournamentIdentity(String tournamentIdentity) {
        this.tournamentIdentity = tournamentIdentity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getEgfPin() {
        return egfPin;
    }

    public void setEgfPin(String egfPin) {
        this.egfPin = egfPin;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }
}
