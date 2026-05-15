package com.example.chatauth;

import javafx.application.Application;
import javafx.stage.Stage;

// Class chính để khởi chạy ứng dụng ChatApp
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Tạo service xử lý đăng nhập và quên mật khẩu
        AuthService authService = new AuthService();

        // Mở màn hình đăng nhập khi chạy chương trình
        LoginView loginView = new LoginView(primaryStage, authService);
        loginView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
