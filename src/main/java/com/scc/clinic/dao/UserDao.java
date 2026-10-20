package com.scc.clinic.dao;

import com.scc.clinic.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public boolean usernameExists(String username) throws SQLException {
        User user = findByUsername(username);
        return user != null;
    }

    public void register(String fullName, String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO tbl_accounts (a_full_name, a_username, a_password, a_role, a_status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, hashPassword(password));
            ps.setString(4, role);
            ps.setString(5, "PENDING");
            ps.executeUpdate();
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM tbl_accounts WHERE LOWER(a_username) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("a_id"),
                        rs.getString("a_full_name"),
                        rs.getString("a_username"),
                        rs.getString("a_password"),
                        rs.getString("a_role"),
                        rs.getString("a_status"),
                        rs.getString("a_created_at"));
                rs.close();
                return user;
            }
            rs.close();
            return null;
        }
    }

    public User login(String username, String password) throws SQLException {
        User user = findByUsername(username);

        if (user == null) {
            return null;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword.equals(user.password())) {
            return user;
        }
        return null;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());

            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }
}
