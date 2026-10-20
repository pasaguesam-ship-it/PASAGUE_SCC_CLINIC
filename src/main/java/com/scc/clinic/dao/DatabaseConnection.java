package com.scc.clinic.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
}
