package com.dev;

import com.dev.objects.Message;
import com.dev.objects.UserObject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class Persist {
    private Connection connection;

    @PostConstruct
    public void createConnectionToDatabase() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/task1", "root", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String login(String username, String password) {//front!!!!!
        String token = null;
        if (!doseUserExist(username)) {
            token = "wrongName";}
        else
        {
            if (!checkPassword(username, password) && (wrongLoginTry(username) < 5)) {
                addWrongLogin(username);
                token = "wrongPassword";}
             if(wrongLoginTry(username) >= 5){
                    token = "lockedUser";}
             if (checkPassword(username,password)&&(wrongLoginTry(username) < 5)){
                token = getUserToken(username);}
        }
        return token;
    }

    private String getUserToken(String username) {
        String token=null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT token FROM users WHERE username =?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                token=resultSet.getString("token");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }
    public boolean checkPassword(String username, String password)
    {
        boolean checkPassword = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT password FROM users WHERE username =?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getString("password").equals(password))
                    checkPassword = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return checkPassword;
    }

    public String createHash(String username, String password) {
        String myHash = null;
        try {
            String hash = "35454B055CC325EA1AF2126E27707052";
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((username + password).getBytes());
            byte[] digest = md.digest();
            myHash = DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return myHash;
    }

    public boolean doesUsernameFree(String username) {
        boolean flag = true;
        String tempUsername;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT username FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tempUsername = resultSet.getString("username");
                if (tempUsername.equals(username)) {
                    flag = false;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

//    PreparedStatement preparedStatement = this.connection.prepareStatement(" INSERT INTO users (username,password,token)"
//            + " values (?,?,?)


    public boolean addUser(String username ,String password) {
      String token ="";
      boolean userAdded = false;
        if (doesUsernameFree(username)) {

            token = createHash(username,password);
            userAdded = true;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO users (username,passwors,token) VALUES (?,?,?)");
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            preparedStatement.setString(3,token);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
          }


          return userAdded;
    }

    public String getUsernameByToken(String token) {
        String username = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT username FROM users WHERE token=?");
            preparedStatement.setString(1, token);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next())
                username = rs.getString("username");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    public List<Message> getMessagesByUsername(String username) {
        List<Message> messages = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM messages WHERE receiver_id=? ORDER BY send_date DESC , send_time DESC");
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setSender(rs.getString("sender_id"));
                message.setReceiver(rs.getString("receiver_id"));
                message.setTitle(rs.getString("title"));
                message.setBody(rs.getString("body"));
                message.setId(rs.getInt("id"));
                message.setRead(rs.getInt("read_or_not"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public boolean doseUserExist(String username) {
        boolean userExist = false;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userExist = true;


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExist;

    }

    public boolean sendMessage (String sender, String receiver ,String title , String body) {
        boolean receiverUserExist = false;
        boolean messageSent=false;
        try {
            receiverUserExist = doseUserExist(receiver);
            if (receiverUserExist && title.length()>0 && body.length()>0) {
                messageSent=true;
                PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO messages (sender_id, receiver_id, title, body,send_date,send_time)\n" +
                        " VALUES (?,?,?,?,NOW(),NOW())");
                preparedStatement.setString(1, sender);
                preparedStatement.setString(2, receiver);
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, body);
                preparedStatement.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageSent;

    }
//set given message by id to read message. return true after changes.
        public boolean setReadMessage(int messageId){
            try {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "UPDATE messages SET read_or_not = 1, reading_date = NOW() WHERE id = ?");

                preparedStatement.setInt(1, messageId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }

        //delete message by id. return true if succeeded.
        public void deleteMessageById(int messageId){
            try{
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "DELETE FROM messages WHERE id = ?"
                );
                preparedStatement.setInt(1, messageId);
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    public int wrongLoginTry(String username) {
        int wrongTry = 0;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT connection_trys  FROM users WHERE username =?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                wrongTry = resultSet.getInt("connection_trys");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wrongTry;

    }
    public void addWrongLogin (String username)
    {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE users\n" +
                    "SET connection_trys = connection_trys + 1\n WHERE username = ?");
            preparedStatement.setString(1,username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    }





