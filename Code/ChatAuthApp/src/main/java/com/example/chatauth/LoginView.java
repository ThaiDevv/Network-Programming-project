package com.example.chatauth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// Màn hình đăng nhập của ứng dụng ChatApp
public class LoginView {

    private final Stage stage;
    private final AuthService authService;

    public LoginView(Stage stage, AuthService authService) {
        this.stage = stage;
        this.authService = authService;
    }

    public void show() {
        // Tạo tiêu đề và mô tả cho màn hình đăng nhập
        Label titleLabel = new Label("Sign in to ChatApp");
        titleLabel.getStyleClass().add("title-dark");

        Label subtitleLabel = new Label("Enter your account to continue");
        subtitleLabel.getStyleClass().add("subtitle-dark");

        VBox headerBox = new VBox(6, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);

        // Ô nhập tài khoản và mật khẩu
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username or email");
        usernameField.getStyleClass().add("input-dark");
        usernameField.setPrefWidth(360);
        usernameField.setMaxWidth(360);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input-dark");
        passwordField.setPrefWidth(360);
        passwordField.setMaxWidth(360);

        // Nút đăng nhập và nút quên mật khẩu
        Button loginButton = new Button("Continue");
        loginButton.getStyleClass().add("btn-primary-dark");
        loginButton.setPrefWidth(360);
        loginButton.setMaxWidth(360);

        Button forgotPasswordButton = new Button("Forgot password?");
        forgotPasswordButton.getStyleClass().add("btn-link-dark");

        // Dòng chữ footer ở cuối form
        Label footerLabel = new Label("Chat Application via TCP/IP");
        footerLabel.getStyleClass().add("footer-text");
        VBox.setMargin(footerLabel, new Insets(16, 0, 0, 0));

        // Gom tất cả vào một card ở giữa màn hình
        VBox card = new VBox(18,
                headerBox,
                usernameField,
                passwordField,
                loginButton,
                forgotPasswordButton,
                footerLabel
        );
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("card-dark");
        card.setMaxWidth(430);

        StackPane root = new StackPane(card);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-dark");

        // Xử lý khi người dùng bấm nút Continue
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            // Kiểm tra người dùng đã nhập đủ thông tin chưa
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error",
                        "Please fill in all fields.");
                return;
            }

            // Gọi AuthService để kiểm tra tài khoản trong database
            if (authService.login(username, password)) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Login successful.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed",
                        "Wrong username or password.");
            }
        });

        // Chuyển sang màn hình quên mật khẩu
        forgotPasswordButton.setOnAction(e -> {
            ForgotPasswordView forgotPasswordView = new ForgotPasswordView(stage, authService);
            forgotPasswordView.show();
        });

        // Tạo scene và hiển thị lên stage
        Scene scene = new Scene(root, 560, 450);
        scene.getStylesheets().add(
                getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("ChatApp - Sign In");
        stage.setScene(scene);
        stage.show();
    }

    // Hiển thị hộp thoại thông báo cho người dùng
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
