package com.example.chatauth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt;

// Class xử lý đăng nhập và đặt lại mật khẩu, kết nối trực tiếp với database MySQL
public class AuthService {

    // Hàm đăng nhập: kiểm tra tài khoản trong bảng users
    public boolean login(String usernameOrEmail, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ? OR email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    // Không tìm thấy tài khoản
                    return false;
                }

                // Lấy password_hash từ database để so sánh
                String passwordHash = rs.getString("password_hash");

                // Nếu mật khẩu đã được hash thì kiểm tra bằng BCrypt
                if (passwordHash != null &&
                        (passwordHash.startsWith("$2a$") ||
                         passwordHash.startsWith("$2b$") ||
                         passwordHash.startsWith("$2y$"))) {
                    return BCrypt.checkpw(password, passwordHash);
                }

                // Nếu là tài khoản test cũ thì so sánh trực tiếp
                return password.equals(passwordHash);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm đặt lại mật khẩu: kiểm tra username + email rồi cập nhật password mới
    public boolean resetPassword(String username, String email, String newPassword) {
        String checkSql = "SELECT id FROM users WHERE username = ? AND email = ?";
        String updateSql = "UPDATE users SET password_hash = ? WHERE username = ? AND email = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Kiểm tra xem tài khoản có tồn tại không
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, username);
                checkPs.setString(2, email);

                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                }
            }

            // Mã hóa mật khẩu mới bằng BCrypt trước khi lưu vào database
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setString(1, hashedPassword);
                updatePs.setString(2, username);
                updatePs.setString(3, email);

                int rowsUpdated = updatePs.executeUpdate();
                return rowsUpdated > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
