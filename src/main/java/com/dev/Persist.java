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

    public String login(String username, String password) {
        String token = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT username FROM users WHERE username = ?");//if there is no username
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                token = "0";
            } else {
                PreparedStatement preparedStatement1 = this.connection.prepareStatement(
                        "SELECT password FROM users WHERE password = ?");//if user enter wrong password
                preparedStatement1.setString(1, password);
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                if (!resultSet1.next()) {
                    token = "1";
                } else {
                    PreparedStatement preparedStatement2 = this.connection.prepareStatement(
                            "SELECT token FROM users WHERE username = ? AND password=?");//if success return token
                    preparedStatement2.setString(1, username);
                    preparedStatement2.setString(2, password);
                    ResultSet resultSet2 = preparedStatement2.executeQuery();
                    if (resultSet2.next()) {
                        token = resultSet2.getString("token");
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;

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

    public void addUser(UserObject userObject) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(" INSERT INTO users (username,password,token)"
                    + " values (?,?,?)");
            preparedStatement.setString(1, userObject.getUsername());
            preparedStatement.setString(2, userObject.getPassword());
            preparedStatement.setString(3, userObject.getToken());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public List<Message> getMessageByUserName(String username) {
        List<Message> messages = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM messages WHERE receiver_id=?");
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setSender(rs.getString("sender_id"));
                message.setReceiver(rs.getString("receiver_id"));
                message.setTitle(rs.getString("title"));
                message.setBody(rs.getString("body"));
                message.setId(rs.getInt("id"));
                message.setId(rs.getInt("read_or_not"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public boolean doseUserExist(String userName) {
        boolean userExist = false;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userExist = true;


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExist;

    }

    public boolean sendMessage (String senderName , String receiverName ,String title , String body) {

        boolean receiverUserExist = false;
        try {
            receiverUserExist = doseUserExist(receiverName);
            if (receiverUserExist) {
                PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO messages (sender_id, receiver_id, title, body,send_date)\n" +
                        " VALUES (?,?,?,?,NOW())");
                preparedStatement.setString(1, senderName);
                preparedStatement.setString(2, receiverName);
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, body);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receiverUserExist;

    }
//set given message by id to read message. return true after changes.
        public boolean setReadMessage(int messageId){
            try {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "UPDATE messages\n" +
                                "SET read_or_not = ?, reading_date = NOW() \n" +
                                "WHERE id = ?");
                preparedStatement.setInt(1, 1);
                preparedStatement.setInt(2, messageId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }

        //delete message by id. return true if succeeded.
        public boolean deleteMessageById(int messageId){
            try{
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "DELETE FROM messages WHERE id = ?"
                );
                preparedStatement.setInt(1, messageId);
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return true;
        }


    }





