package com.midooabdaim.midooabdaimchat.data.model;

public class Data {

    private String user, title, body, sented;
    private int icon;


    public Data(String user, int icon, String body, String title, String sented) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.sented = sented;
        this.icon = icon;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
