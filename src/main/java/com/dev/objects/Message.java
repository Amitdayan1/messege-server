package com.dev.objects;



public class Message {
    private String sender;
    private String receiver;
    private String title;

    public Message(String sender, String receiver, String title, String body) {
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.body = body;
    }
    public Message() {
        this.sender ="";
        this.receiver = "";
        this.title = "";
        this.body = "";
    }

    private String body;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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
}