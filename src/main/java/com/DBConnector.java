package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnector {

    private static final String URL = "jdbc:postgresql://localhost:5432/password_manager";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "altaya";

    public void addMaster(String newUsername, String newPassword) throws SQLException {
        if (doesUserExist(newUsername)) {
            throw new SQLException("Username already exists.");
        }

        String sql = "INSERT INTO master(name, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newUsername);
            pstmt.setString(2, newPassword);

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted == 0) {
                throw new SQLException("User was not inserted into the database.");
            }

        }
    }

    public int getMasterIdByUsername(String username) throws SQLException {
        String sql = "SELECT user_id FROM master WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                } else {
                    throw new SQLException("No master found with the username: " + username);
                }
            }
        }
    }

    public void addLogIn(String master, String newUsername, String newPassword) throws SQLException {
        int id = getMasterIdByUsername(master);
        String insertSql = "INSERT INTO vault(master_id, username, password) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD)) {
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, id);
                pstmt.setString(2, newUsername);
                pstmt.setString(3, newPassword);

                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted == 0) {
                    throw new SQLException("User was not inserted into the database.");
                }
            }
        }
    }

    public boolean doesUserExist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM master WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        }
    }

    public String retrieveHashedPassword(String master) throws SQLException {
        String sql = "SELECT password FROM master WHERE name = ?";
        String out = "";
        if (doesUserExist(master)) {

            try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, master);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    out = rs.getString(1);
                }

            }
        }
        return out;
    }

    public void deleteMaster(String master, String password) {
        // TODO: Finish this
    }
}
