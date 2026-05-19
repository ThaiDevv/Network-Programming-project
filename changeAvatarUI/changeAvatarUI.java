import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImagePattern;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class AvatarUI extends Application {

    private Image currentAvatarImage =
            new Image("file:avatarMacDinh.jpg");

    private Circle currentAvatarCircle;

    private Label toast;

    private Image selectedImage;

    @Override
    public void start(Stage primaryStage) {

        // ROOT
        StackPane root = new StackPane();

        root.setStyle("""
                -fx-background-color: #111;
                """);

        // PROFILE AREA
        VBox profileArea = new VBox(20);
        profileArea.setAlignment(Pos.CENTER);

        currentAvatarCircle = new Circle(90);

        currentAvatarCircle.setFill(
                new ImagePattern(currentAvatarImage)
        );

        currentAvatarCircle.setStroke(Color.web("#333"));
        currentAvatarCircle.setStrokeWidth(4);

        Button changeAvatarBtn =
                new Button("Đổi avatar");

        changeAvatarBtn.setStyle("""
                -fx-background-color: #1877f2;
                -fx-text-fill: white;
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-background-radius: 14px;
                -fx-padding: 14 26;
                -fx-cursor: hand;
                """);

        profileArea.getChildren().addAll(
                currentAvatarCircle,
                changeAvatarBtn
        );

        // TOAST
        toast = new Label(
                "Thành công đổi avatar"
        );

        toast.setVisible(false);

        toast.setStyle("""
                -fx-background-color: #1f883d;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 14 22;
                -fx-background-radius: 14px;
                """);

        StackPane.setAlignment(
                toast,
                Pos.BOTTOM_RIGHT
        );

        StackPane.setMargin(
                toast,
                new Insets(0, 30, 30, 0)
        );

        root.getChildren().addAll(
                profileArea,
                toast
        );

        // ACTION
        changeAvatarBtn.setOnAction(e -> {

            openAvatarModal(primaryStage);
        });

        Scene scene =
                new Scene(root, 1200, 800);

        primaryStage.setTitle("Avatar UI");

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void openAvatarModal(Stage owner) {

        Stage modal = new Stage();

        modal.initOwner(owner);

        modal.initModality(Modality.APPLICATION_MODAL);

        // MAIN LAYOUT
        BorderPane root = new BorderPane();

        root.setStyle("""
                -fx-background-color: #1c1c1c;
                """);

        // HEADER
        HBox header = new HBox();

        header.setAlignment(Pos.CENTER);

        header.setPrefHeight(80);

        header.setStyle("""
                -fx-border-color: transparent transparent #333 transparent;
                """);

        Label title =
                new Label("Chọn ảnh đại diện");

        title.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 28px;
                -fx-font-weight: bold;
                """);

        header.getChildren().add(title);

        // BODY
        HBox body = new HBox();

        // LEFT
        VBox leftPanel = new VBox();

        leftPanel.setAlignment(Pos.CENTER);

        leftPanel.setPadding(new Insets(30));

        leftPanel.setPrefWidth(700);

        leftPanel.setStyle("""
                -fx-background-color: #151515;
                """);

        StackPane previewContainer =
                new StackPane();

        previewContainer.setPrefSize(520, 520);

        previewContainer.setMaxSize(520, 520);

        previewContainer.setStyle("""
                -fx-background-color: black;
                -fx-background-radius: 18px;
                """);

        ImageView previewImage =
                new ImageView(currentAvatarImage);

        previewImage.setFitWidth(500);

        previewImage.setFitHeight(500);

        previewImage.setPreserveRatio(true);

        Circle clip = new Circle(250);

        clip.centerXProperty().bind(
                previewContainer.widthProperty()
                        .divide(2)
        );

        clip.centerYProperty().bind(
                previewContainer.heightProperty()
                        .divide(2)
        );

        previewImage.setClip(clip);

        selectedImage = currentAvatarImage;

        previewContainer.getChildren()
                .add(previewImage);

        leftPanel.getChildren()
                .add(previewContainer);

        // RIGHT
        VBox rightPanel = new VBox(20);

        rightPanel.setPadding(new Insets(24));

        rightPanel.setPrefWidth(340);

        rightPanel.setStyle("""
                -fx-border-color: transparent transparent transparent #333;
                """);

        Label oldAvatarTitle =
                new Label(
                        "Ảnh đại diện đã từng dùng"
                );

        oldAvatarTitle.setStyle("""
                -fx-text-fill: #ddd;
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                """);

        TilePane avatarGrid =
                new TilePane();

        avatarGrid.setHgap(12);

        avatarGrid.setVgap(12);

        avatarGrid.setPrefColumns(3);

        // OLD AVATARS
        String[] avatars = {
                "https://i.pravatar.cc/300?img=1",
                "https://i.pravatar.cc/300?img=2",
                "https://i.pravatar.cc/300?img=3",
                "https://i.pravatar.cc/300?img=4",
                "https://i.pravatar.cc/300?img=5"
        };

        for (String url : avatars) {

            Image img =
                    new Image(url);

            ImageView avatar =
                    new ImageView(img);

            avatar.setFitWidth(90);

            avatar.setFitHeight(90);

            avatar.setPreserveRatio(false);

            avatar.setStyle("""
                    -fx-cursor: hand;
                    """);

            avatar.setOnMouseClicked(e -> {

                selectedImage = img;

                previewImage.setImage(img);
            });

            avatarGrid.getChildren()
                    .add(avatar);
        }

        // BUTTONS
        VBox actions = new VBox(12);

        Button uploadBtn =
                new Button("Tải ảnh lên");

        Button removeBtn =
                new Button("Xóa avatar");

        uploadBtn.setMaxWidth(Double.MAX_VALUE);
        removeBtn.setMaxWidth(Double.MAX_VALUE);

        uploadBtn.setPrefHeight(52);
        removeBtn.setPrefHeight(52);

        uploadBtn.setStyle("""
                -fx-background-color: #1877f2;
                -fx-text-fill: white;
                -fx-font-size: 15px;
                -fx-font-weight: bold;
                -fx-background-radius: 14px;
                -fx-cursor: hand;
                """);

        removeBtn.setStyle("""
                -fx-background-color: #2d2d2d;
                -fx-text-fill: white;
                -fx-font-size: 15px;
                -fx-font-weight: bold;
                -fx-background-radius: 14px;
                -fx-cursor: hand;
                """);

        // UPLOAD
        uploadBtn.setOnAction(e -> {

            FileChooser chooser =
                    new FileChooser();

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Image Files",
                            "*.png",
                            "*.jpg",
                            "*.jpeg"
                    )
            );

            File file =
                    chooser.showOpenDialog(modal);

            if (file != null) {

                Image img =
                        new Image(
                                file.toURI().toString()
                        );

                selectedImage = img;

                previewImage.setImage(img);
            }
        });

        // REMOVE
        removeBtn.setOnAction(e -> {

            Image defaultImg =
                    new Image(
                            "file:avatarMacDinh.jpg"
                    );

            selectedImage = defaultImg;

            previewImage.setImage(defaultImg);
        });

        actions.getChildren().addAll(
                uploadBtn,
                removeBtn
        );

        // FOOTER
        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox footer =
                new HBox(12);

        Button cancelBtn =
                new Button("Hủy");

        Button saveBtn =
                new Button("Lưu");

        cancelBtn.setPrefHeight(50);
        saveBtn.setPrefHeight(50);

        cancelBtn.setPrefWidth(140);
        saveBtn.setPrefWidth(140);

        cancelBtn.setStyle("""
                -fx-background-color: #333;
                -fx-text-fill: white;
                -fx-font-size: 15px;
                -fx-font-weight: bold;
                -fx-background-radius: 12px;
                -fx-cursor: hand;
                """);

        saveBtn.setStyle("""
                -fx-background-color: #1877f2;
                -fx-text-fill: white;
                -fx-font-size: 15px;
                -fx-font-weight: bold;
                -fx-background-radius: 12px;
                -fx-cursor: hand;
                """);

        cancelBtn.setOnAction(e -> {

            modal.close();
        });

        saveBtn.setOnAction(e -> {

            currentAvatarImage = selectedImage;

            currentAvatarCircle.setFill(
                    new ImagePattern(currentAvatarImage)
            );

            showToast();

            modal.close();

            /*
                BACKEND WORKFLOW

                1. Upload image server
                2. Save avatar_url MySQL
                3. Emit realtime socket
                4. Refresh UI user
            */
        });

        footer.getChildren().addAll(
                cancelBtn,
                saveBtn
        );

        rightPanel.getChildren().addAll(
                oldAvatarTitle,
                avatarGrid,
                actions,
                spacer,
                footer
        );

        body.getChildren().addAll(
                leftPanel,
                rightPanel
        );

        root.setTop(header);

        root.setCenter(body);

        Scene scene =
                new Scene(root, 1100, 760);

        modal.setScene(scene);

        modal.showAndWait();
    }

    private void showToast() {

        toast.setVisible(true);

        new Thread(() -> {

            try {

                Thread.sleep(2500);

            } catch (Exception ignored) {
            }

            javafx.application.Platform.runLater(() -> {

                toast.setVisible(false);
            });

        }).start();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
