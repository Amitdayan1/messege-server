package com.dev.controllers;

import com.dev.Persist;

import com.dev.objects.Message;
import com.dev.objects.UserObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@RestController
public class TestController {
  // class variables


    @Autowired
    private Persist persist;

    @PostConstruct
    private void init() {
      //initialize

    }

//more annotations

    @RequestMapping(value = "/add-user")//check here what happened when the password empty !! and move to persist
    public boolean addUser(String username ,String password) {
        boolean isFree;
        isFree=persist.doesUsernameFree(username);
        if (isFree) {
            String token = persist.createHash(username, password);
            UserObject userObject = new UserObject(username,password,token);
            persist.addUser(userObject);
        }
        return isFree;
    }
    @RequestMapping(value = "/log-in")
    public String userLogin(String username,String password){
        return persist.login(username,password);
    }

    @RequestMapping(value = "/get-username-by-token")
    public String getUsernameByToken(String token){
        return persist.getUsernameByToken(token);
    }
    @RequestMapping(value = "/get-messages-by-username")
    public List<Message> getMessageByUserName(String username){
        return persist.getMessagesByUsername(username);
    }
    @RequestMapping(value = "/send-message")
    public boolean sendMessage (String sender , String receiver ,String title , String body)
    {
        return persist.sendMessage(sender,receiver,title,body);
    }
    //set api request function to change message by id to read message
    @RequestMapping(value = "/set-read-message")
    public void setReadMessage(int messageId){
        persist.setReadMessage(messageId);
    }
    //set api request function to delete message from table cy id message
    @RequestMapping(value = "/delete-message-by-id")
    public void deleteMessageById(int messageId){
        persist.deleteMessageById(messageId);
    }
    @RequestMapping(value = "/dose-user-exist")
    public boolean doseUserExist(String username) {
        return persist.doseUserExist(username);


    }
}


