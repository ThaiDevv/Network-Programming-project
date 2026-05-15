package com.example.chatauth;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// Màn hình quên mật khẩu, cho phép người dùng đặt lại mật khẩu mới
public class ForgotPasswordView {

    private final Stage stage;
    private final AuthService authService;

    public ForgotPasswordView(Stage stage, AuthService authService) {
        this.stage = stage;
        this.authService = authService;
    }

    public void show() {
        // Tiêu đề và mô tả cho màn hình đặt lại mật khẩu
        Label titleLabel = new Label("Reset Password");
        titleLabel.getStyleClass().add("title-dark");

        Label subtitleLabel = new Label("Create a new password for your account");
        subtitleLabel.getStyleClass().add("subtitle-dark");

        VBox headerBox = new VBox(6, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);

        // Người dùng nhập thông tin để đặt lại mật khẩu
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("input-dark");
        usernameField.setPrefWidth(380);
        usernameField.setMaxWidth(380);

        TextField emailField = new TextField();
        emailField.setPromptText("Registered email");
        emailField.getStyleClass().add("input-dark");
        emailField.setPrefWidth(380);
        emailField.setMaxWidth(380);

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");
        newPasswordField.getStyleClass().add("input-dark");
        newPasswordField.setPrefWidth(380);
        newPasswordField.setMaxWidth(380);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        confirmPasswordField.getStyleClass().add("input-dark");
        confirmPasswordField.setPrefWidth(380);
        confirmPasswordField.setMaxWidth(380);

        // Nút đặt lại mật khẩu và nút quay lại
        Button resetButton = new Button("Reset Password");
        resetButton.getStyleClass().add("btn-primary-dark");
        resetButton.setPrefWidth(380);
        resetButton.setMaxWidth(380);

        Button backButton = new Button("Back to Login");
        backButton.getStyleClass().add("btn-link-dark");

        // Gom tất cả vào card ở giữa màn hình
        VBox card = new VBox(16,
                headerBox,
                usernameField,
                emailField,
                newPasswordField,
                confirmPasswordField,
                resetButton,
                backButton
        );
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("card-dark");
        card.setMaxWidth(460);

        StackPane root = new StackPane(card);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-dark");

        // Xử lý khi bấm nút Reset Password
        resetButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Kiểm tra người dùng đã nhập đủ thông tin chưa
            if (username.isEmpty() || email.isEmpty()
                    || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Please fill in all fields.");
                return;
            }

            // Mật khẩu phải có ít nhất 6 ký tự
            if (newPassword.length() < 6) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Password must be at least 6 characters.");
                return;
            }

            // Kiểm tra mật khẩu mới và xác nhận mật khẩu có trùng nhau không
            if (!newPassword.equals(confirmPassword)) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Passwords do not match.");
                return;
            }

            // Nếu thông tin đúng thì cập nhật mật khẩu mới vào database
            if (authService.resetPassword(username, email, newPassword)) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Password reset successful.");
                // Quay lại màn hình đăng nhập
                LoginView loginView = new LoginView(stage, authService);
                loginView.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Reset Failed",
                        "Information is incorrect.");
            }
        });

        // Bấm nút Back thì quay lại màn hình đăng nhập
        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView(stage, authService);
            loginView.show();
        });

        // Tạo scene và hiển thị
        Scene scene = new Scene(root, 580, 560);
        scene.getStylesheets().add(
                getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("ChatApp - Reset Password");
        stage.setScene(scene);
        stage.show();
    }

    // Hiển thị hộp thoại thông báo
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
