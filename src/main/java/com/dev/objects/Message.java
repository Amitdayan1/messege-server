package com.dev.objects;



public class Message {
    private String sender;
    private String receiver;
    private String title;
    private int id ;
    private int read ;

    public Message(String sender, String receiver, String title, String body, int id  ,int read ) {
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.body = body;
        this.id = id;
        this.read = read;
    }

    public Message() {
        this.sender ="";
        this.receiver = "";
        this.title = "";
        this.body = "";
        this.id =-1;
        this.read = 0 ;
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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }
}
