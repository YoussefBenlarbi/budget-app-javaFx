package com.example.budget.Repository;

import com.example.budget.Entity.User;
import com.example.budget.utils.DbConnect;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository {
    public void save(User user) {
        String insertUserSQL = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DbConnect.getConnect();
             PreparedStatement preparedStatement = conn.prepareStatement(insertUserSQL)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public User select(int id) {
        String selectUserSQL = "SELECT * FROM users WHERE id = ?";
        User user = null;
        try (Connection conn = DbConnect.getConnect();
             PreparedStatement preparedStatement = conn.prepareStatement(selectUserSQL)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }


    public void delete(int id) {
        String deleteUserSQL = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DbConnect.getConnect();
             PreparedStatement preparedStatement = conn.prepareStatement(deleteUserSQL)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(User user) {
        String updateUserSQL = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";
        try (Connection conn = DbConnect.getConnect();
             PreparedStatement preparedStatement = conn.prepareStatement(updateUserSQL)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setInt(5, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User login(String username, String password) {
        String selectUserSQL = "SELECT * FROM users WHERE username = ?";
        User user = null;

        try (Connection conn = DbConnect.getConnect();
             PreparedStatement preparedStatement = conn.prepareStatement(selectUserSQL)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");

                    // Direct comparison of passwords
                    if (password.equals(storedPassword)) {
                        user = new User(
                                resultSet.getInt("id"),
                                resultSet.getString("username"),
                                storedPassword,
                                resultSet.getString("email")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, e);
        }
        return user;
    }
    public List<User> getAll() throws Exception {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DbConnect.getConnect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                ));
            }
        }
        return users;
    }
    //TODO method to hash the password .
//    private String hashPassword(String password) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
//            return Base64.getEncoder().encodeToString(encodedHash);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    //TODO Login method to log in and check if the hash password is correct .

//    public User login(String username, String password) {
//        String selectUserSQL = "SELECT * FROM users WHERE username = ?";
//        User user = null;
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement preparedStatement = conn.prepareStatement(selectUserSQL)) {
//
//            preparedStatement.setString(1, username);
//
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    String storedPassword = resultSet.getString("password");
//                    String hashedInputPassword = hashPassword(password);
//                    if (hashedInputPassword != null && hashedInputPassword.equals(storedPassword)) {
//                        user = new User(
//                                resultSet.getInt("id"),
//                                resultSet.getString("username"),
//                                storedPassword,
//                                resultSet.getString("email")
//                        );
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Consider logging the error or throwing a custom exception
//        }
//        return user;
//    }
    //TODO Register method to register a new user .
//    public boolean register(User user) {
//        // First, check if the username or email already exists
//        if (isUsernameTaken(user.getUsername()) || isEmailTaken(user.getEmail())) {
//            return false;
//        }
//
//        // If username and email are available, proceed with registration
//        String insertUserSQL = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement preparedStatement = conn.prepareStatement(insertUserSQL)) {
//
//            preparedStatement.setString(1, user.getUsername());
//            preparedStatement.setString(2, hashPassword(user.getPassword()));
//            preparedStatement.setString(3, user.getEmail());
//
//            int affectedRows = preparedStatement.executeUpdate();
//            return affectedRows > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Consider logging the error or throwing a custom exception
//            return false;
//        }
//    }
//
    // TODO method to check if the username or email already exists .
//    private boolean isUsernameTaken(String username) {
//        String checkUsernameSQL = "SELECT COUNT(*) FROM users WHERE username = ?";
//        return checkIfExists(checkUsernameSQL, username);
//    }

    //TODO method to check if the username or email already exists.

//    private boolean isEmailTaken(String email) {
//        String checkEmailSQL = "SELECT COUNT(*) FROM users WHERE email = ?";
//        return checkIfExists(checkEmailSQL, email);
//    }
//
    //TODO method to check if the username or email already exists .
//    private boolean checkIfExists(String sql, String parameter) {
//        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
//
//            preparedStatement.setString(1, parameter);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    return resultSet.getInt(1) > 0;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Consider logging the error or throwing a custom exception
//        }
//        return false;
//    }
}
