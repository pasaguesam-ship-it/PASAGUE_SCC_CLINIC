package com.scc.clinic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initialize() {
        String students = "CREATE TABLE IF NOT EXISTS tbl_students ("
                + "s_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "s_student_no TEXT NOT NULL UNIQUE, "
                + "s_first_name TEXT NOT NULL, "
                + "s_last_name TEXT NOT NULL, "
                + "s_course TEXT NOT NULL, "
                + "s_year_level INTEGER NOT NULL, "
                + "s_contact_no TEXT, "
                + "s_emergency_contact TEXT, "
                + "s_status TEXT NOT NULL DEFAULT 'ACTIVE')";

        String accounts = "CREATE TABLE IF NOT EXISTS tbl_accounts ("
                + "a_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "s_id INTEGER UNIQUE, "
                + "a_full_name TEXT NOT NULL, "
                + "a_username TEXT NOT NULL UNIQUE, "
                + "a_password TEXT NOT NULL, "
                + "a_role TEXT NOT NULL, "
                + "a_status TEXT NOT NULL DEFAULT 'PENDING', "
                + "a_created_at TEXT DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (s_id) REFERENCES tbl_students(s_id))";

        String medicines = "CREATE TABLE IF NOT EXISTS tbl_medicines ("
                + "m_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "m_name TEXT NOT NULL, "
                + "m_dosage TEXT NOT NULL, "
                + "m_quantity INTEGER NOT NULL DEFAULT 0, "
                + "m_unit TEXT NOT NULL, "
                + "m_reorder_level INTEGER NOT NULL DEFAULT 10, "
                + "m_expiry_date TEXT NOT NULL, "
                + "m_status TEXT NOT NULL DEFAULT 'AVAILABLE')";

        String visits = "CREATE TABLE IF NOT EXISTS tbl_visits ("
                + "v_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "s_id INTEGER NOT NULL, "
                + "a_id INTEGER NOT NULL, "
                + "v_visit_date TEXT DEFAULT CURRENT_TIMESTAMP, "
                + "v_complaint_type TEXT NOT NULL, "
                + "v_complaint_details TEXT, "
                + "v_diagnosis TEXT, "
                + "v_treatment TEXT, "
                + "v_status TEXT NOT NULL DEFAULT 'ACTIVE', "
                + "FOREIGN KEY (s_id) REFERENCES tbl_students(s_id), "
                + "FOREIGN KEY (a_id) REFERENCES tbl_accounts(a_id))";

        String dispensed = "CREATE TABLE IF NOT EXISTS tbl_dispensed ("
                + "d_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "v_id INTEGER NOT NULL, "
                + "m_id INTEGER NOT NULL, "
                + "d_quantity INTEGER NOT NULL, "
                + "d_date_dispensed TEXT DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (v_id) REFERENCES tbl_visits(v_id), "
                + "FOREIGN KEY (m_id) REFERENCES tbl_medicines(m_id))";

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

        try (Connection conn = DatabaseConnection.getConnection()) {
            createTable(conn, students);
            createTable(conn, accounts);
            createTable(conn, medicines);
            createTable(conn, visits);
            createTable(conn, dispensed);
            createTable(conn, logs);
            addDefaultAdmin(conn);
            System.out.println("Database initialized.");
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
