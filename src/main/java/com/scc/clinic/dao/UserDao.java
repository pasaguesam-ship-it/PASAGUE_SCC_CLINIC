package com.scc.clinic.dao;

import com.scc.clinic.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public boolean usernameExists(String username) throws SQLException {
        User user = findByUsername(username);
        return user != null;
    }

    public void register(String fullName, String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO tbl_accounts (a_full_name, a_username, a_password, a_role, a_status) VALUES (?, ?, ?, ?, ?)";

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

            User user = null;
            if (rs.next()) {
                user = toUser(rs);
            }
            rs.close();
            return user;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM tbl_accounts ORDER BY a_id";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(toUser(rs));
            }
            rs.close();
        }
        return users;
    }

    public List<User> getPendingUsers() throws SQLException {
        String sql = "SELECT * FROM tbl_accounts WHERE a_status = ? ORDER BY a_id";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "PENDING");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(toUser(rs));
            }
            rs.close();
        }
        return users;
    }

    public boolean approveUser(int userId) throws SQLException {
        String sql = "UPDATE tbl_accounts SET a_status = ? WHERE a_id = ? AND a_status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "APPROVED");
            ps.setInt(2, userId);
            ps.setString(3, "PENDING");
            int rows = ps.executeUpdate();
            return rows > 0;
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

    private User toUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("a_id"),
                rs.getString("a_full_name"),
                rs.getString("a_username"),
                rs.getString("a_password"),
                rs.getString("a_role"),
                rs.getString("a_status"),
                rs.getString("a_created_at"));
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
