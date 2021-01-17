package com.example.nitdurgapur.nitdgpchat;

public class Messages {
    private String Date, Message, Time, from, to, type, messageID, name, extension;

    public Messages() {

    }

    public Messages(String date, String message, String time, String from, String to, String type, String messageID, String name, String extension) {
        Date = date;
        Message = message;
        Time = time;
        this.from = from;
        this.to = to;
        this.type = type;
        this.messageID = messageID;
        this.name = name;
        this.extension = extension;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String checker) {
        this.extension = checker;
    }
}
