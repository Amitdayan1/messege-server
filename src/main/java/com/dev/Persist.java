package com.dev;

import com.dev.objects.UserObject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

@Component
public class Persist {
    private Connection connection;

    @PostConstruct
    public void createConnectionToDatabase () {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/task1", "root", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String login (String username, String password) {
        String token = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT token FROM users WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                token = resultSet.getString("token");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;

    }
    public String createHash (String username, String password) {
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
}

