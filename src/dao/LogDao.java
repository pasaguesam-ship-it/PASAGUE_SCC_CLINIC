package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogDao {

    public void addLog(Integer accountId, String action, String username, String details) {
        String sql = "INSERT INTO tbl_logs (a_id, l_action, l_username_entered, l_table_affected, l_record_id, l_details) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, accountId);
            ps.setString(2, action);
            ps.setString(3, username);
            ps.setString(4, "tbl_accounts");
            ps.setObject(5, accountId);
            ps.setString(6, details);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not save log: " + e.getMessage());
        }
    }
}
