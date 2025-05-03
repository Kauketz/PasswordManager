package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {

    private static final String URL = "jdbc:postgresql://localhost:5432/password_manager"; // !DATABASE PORT + NAME HERE
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "altaya"; // ! WRITE PASSWORD HERE

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

    public String getServiceUsername(String master) throws SQLException {
        int id = getMasterIdByUsername(master);
        String selectSql = "SELECT username FROM vault WHERE master_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD)) {
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setInt(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public String getServiceDomain(String master) throws SQLException {
        int id = getMasterIdByUsername(master);
        String selectSql = "SELECT domain FROM vault WHERE master_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD)) {
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setInt(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("domain");
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public List<VaultEntry> getVaultEntriesByMasterId(int masterId) throws SQLException {
        String sql = "SELECT domain, username, password FROM vault WHERE master_id = ?";
        List<VaultEntry> vaultEntries = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, masterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String domain = rs.getString("domain");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    vaultEntries.add(new VaultEntry(domain, username, password));
                }
            }
        }
        return vaultEntries;
    }

    public void deleteVaultEntry(String domain, String username, String masterUsername) throws SQLException {
        int masterId = getMasterIdByUsername(masterUsername);
        String deleteSql = "DELETE FROM vault WHERE master_id = ? AND domain = ? AND username = ?";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {

            pstmt.setInt(1, masterId);
            pstmt.setString(2, domain);
            pstmt.setString(3, username);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new SQLException("Failed to delete the vault entry.");
            }
        }
    }

    public String getServicePassword(String master) throws SQLException, Exception {
        int id = getMasterIdByUsername(master);
        String selectSql = "SELECT password FROM vault WHERE master_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD)) {
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setInt(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String encryptedPassword = rs.getString("password");
                        return PasswordHandling.decryptServicePassword(encryptedPassword, master);
                    } else {
                        return null;
                    }
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
