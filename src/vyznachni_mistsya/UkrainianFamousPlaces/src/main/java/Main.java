import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.PasswordField;
import java.util.Optional;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        // Initialize login dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("MySQL Authorization");
        dialog.setHeaderText("Enter password to connect to the database:");

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        dialog.getDialogPane().setContent(passwordField);

        Platform.runLater(() -> passwordField.requestFocus());

        // Map button to input
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return passwordField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            // Assigning unencrypted credentials to a static field keeps sensitive data accessible in memory for the duration of the process.
            SiteController.dbPassword = result.get();

            // Load main UI file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Mainview.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Famous Places of Ukraine");
            stage.setScene(scene);

            // Manage application shutdown
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

            stage.show();

        } else {
            // Terminate the process
            Platform.exit();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}