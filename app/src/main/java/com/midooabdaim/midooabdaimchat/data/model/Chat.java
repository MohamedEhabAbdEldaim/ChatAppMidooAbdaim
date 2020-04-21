package com.midooabdaim.midooabdaimchat.data.model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String time;
    private boolean isSeen;


    public Chat(String sender, String receiver, String message, String time, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.isSeen = isSeen;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Chat() {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReciever(String reciever) {
        this.receiver = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}

