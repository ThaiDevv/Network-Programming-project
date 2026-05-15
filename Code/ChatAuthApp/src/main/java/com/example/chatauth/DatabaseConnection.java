package com.example.chatauth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Class tạo kết nối đến database MySQL của nhóm
public class DatabaseConnection {

    // Thông tin kết nối database
    // Khi nộp thật nên đưa mật khẩu ra file cấu hình hoặc biến môi trường
    private static final String URL =
            "jdbc:mysql://free02.123host.vn:3306/roacqgfa_ltm?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "roacqgfa_ltm";
    private static final String PASSWORD = "11111111";

    // Tạo kết nối đến MySQL, mỗi lần gọi sẽ trả về 1 connection mới
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
