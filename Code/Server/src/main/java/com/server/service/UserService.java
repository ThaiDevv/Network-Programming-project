package com.server.service;

import com.server.config.Database;
import com.server.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public boolean registerUser(User user) {
        // 1. Kiểm tra xem username hoặc email đã tồn tại trong DB chưa
        String checkSql = "SELECT id FROM users WHERE username = ? OR email = ?";
        try (Connection conn = Database.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, user.getUsername());
            checkStmt.setString(2, user.getEmail());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Nếu tìm thấy kết quả nghĩa là user hoặc email đã tồn tại
                return false;
            }
        } catch (Exception e) {
            logger.error("Error checking existing user", e);
            return false;
        }

        // 2. Insert user mới vào DB
        String insertSql = "INSERT INTO users (username, password_hash, email, avatar_url) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
                PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, user.getUsername());
            insertStmt.setString(2, user.getPasswordHash());
            insertStmt.setString(3, user.getEmail());
            insertStmt.setString(4, user.getAvatarUrl());
            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            logger.error("Error inserting new user", e);
            return false;
        }
    }
}
