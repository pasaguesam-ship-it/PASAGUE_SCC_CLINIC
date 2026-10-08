package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:clinic.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        PreparedStatement ps = conn.prepareStatement("PRAGMA foreign_keys = ON");
        ps.execute();
        ps.close();
        return conn;
    }

    public static void initialize() {
        String accounts = "CREATE TABLE IF NOT EXISTS tbl_accounts ("
                + "a_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "a_full_name TEXT NOT NULL, "
                + "a_username TEXT NOT NULL UNIQUE, "
                + "a_password TEXT NOT NULL, "
                + "a_role TEXT NOT NULL, "
                + "a_status TEXT NOT NULL DEFAULT 'PENDING', "
                + "a_created_at TEXT DEFAULT CURRENT_TIMESTAMP)";

        String logs = "CREATE TABLE IF NOT EXISTS tbl_logs ("
                + "l_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "a_id INTEGER, "
                + "l_action TEXT NOT NULL, "
                + "l_username_entered TEXT, "
                + "l_table_affected TEXT, "
                + "l_record_id INTEGER, "
                + "l_details TEXT, "
                + "l_timestamp TEXT DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (a_id) REFERENCES tbl_accounts(a_id))";

        try (Connection conn = getConnection()) {
            createTable(conn, accounts);
            createTable(conn, logs);
            addDefaultAdmin(conn);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void createTable(Connection conn, String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.execute();
        ps.close();
    }

    private static void addDefaultAdmin(Connection conn) throws SQLException {
        PreparedStatement check = conn.prepareStatement(
                "SELECT a_id FROM tbl_accounts WHERE a_role = 'ADMIN'");
        ResultSet rs = check.executeQuery();
        boolean hasAdmin = rs.next();
        rs.close();
        check.close();

        if (hasAdmin) {
            return;
        }

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tbl_accounts (a_full_name, a_username, a_password, a_role, a_status) "
                + "VALUES (?, ?, ?, ?, ?)");
        ps.setString(1, "System Administrator");
        ps.setString(2, "admin");
        ps.setString(3, UserDao.hashPassword("admin123"));
        ps.setString(4, "ADMIN");
        ps.setString(5, "ACTIVE");
        ps.executeUpdate();
        ps.close();
    }
}
