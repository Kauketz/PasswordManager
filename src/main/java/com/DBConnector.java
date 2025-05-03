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

    public void addLogIn(String master, String newUsername, String newPassword, String domain) throws SQLException {
        int id = getMasterIdByUsername(master);
        String insertSql = "INSERT INTO vault(master_id, username, password, domain) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD)) {
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, id);
                pstmt.setString(2, newUsername);
                pstmt.setString(3, newPassword);
                pstmt.setString(4, domain);

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

    public void deleteMaster(String master, String plainPassword) throws SQLException {
        String getUserSql = "SELECT user_id, password FROM master WHERE name = ?";
        String deleteVaultSql = "DELETE FROM vault WHERE master_id = ?";
        String deleteMasterSql = "DELETE FROM master WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD)) {
            conn.setAutoCommit(false);

            try (PreparedStatement getUserStmt = conn.prepareStatement(getUserSql)) {
                getUserStmt.setString(1, master);
                try (ResultSet rs = getUserStmt.executeQuery()) {
                    if (rs.next()) {
                        String dbPassword = rs.getString("password");
                        int userId = rs.getInt("user_id");

                        if (!PasswordHandling.verifyPassword(plainPassword, dbPassword)) {
                            throw new SQLException("Incorrect password. Cannot delete account.");
                        }

                        try (PreparedStatement deleteVaultStmt = conn.prepareStatement(deleteVaultSql)) {
                            deleteVaultStmt.setInt(1, userId);
                            deleteVaultStmt.executeUpdate();
                        }

                        try (PreparedStatement deleteMasterStmt = conn.prepareStatement(deleteMasterSql)) {
                            deleteMasterStmt.setInt(1, userId);
                            deleteMasterStmt.executeUpdate();
                        }

                        conn.commit();
                    } else {
                        throw new SQLException("User not found.");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

}
