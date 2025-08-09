package retail;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

public class LoginController {

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signUpButton;  

    private Connection connect() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/retail_db?useSSL=false&serverTimezone=UTC",
                "root",
                "WJ28@krhps"
            );
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database connection failed: " + e.getMessage());
            return null;
        }
    }

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Admin", "User");

        
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Admin".equalsIgnoreCase(newVal)) {
                signUpButton.setVisible(false);
                signUpButton.setManaged(false);  
            } else if ("User".equalsIgnoreCase(newVal)) {
                signUpButton.setVisible(true);
                signUpButton.setManaged(true);
            } else {
                
                signUpButton.setVisible(false);
                signUpButton.setManaged(false);
            }
        });

        
        signUpButton.setVisible(false);
        signUpButton.setManaged(false);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String role = roleComboBox.getValue();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (role == null || username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Please fill in all fields.");
            return;
        }

        if (role.equalsIgnoreCase("Admin")) {
            if (!username.equals("admin")) {
                showAlert(AlertType.ERROR, "Invalid Admin username");
                return;
            }
            if (validateLogin(username, password, "admin")) {
                showAlert(AlertType.INFORMATION, "Admin login successful!");
                loadAdminDashboard();
            } else {
                showAlert(AlertType.ERROR, "Invalid admin password.");
            }
        } else if (role.equalsIgnoreCase("User")) {
            if (validateLogin(username, password, "user")) {
                showAlert(AlertType.INFORMATION, "User login successful!");
                loadUserView();
            } else {
                showAlert(AlertType.ERROR, "Invalid user credentials.");
            }
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        String role = roleComboBox.getValue();
        if (role == null || !role.equalsIgnoreCase("User")) {
            showAlert(AlertType.WARNING, "Only users can sign up.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("User Sign Up");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Failed to load Sign Up: " + e.getMessage());
        }
    }

    private boolean validateLogin(String username, String password, String role) {
        String query = "SELECT * FROM users WHERE username=? AND password=? AND role=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Login error: " + e.getMessage());
            return false;
        }
    }

    private void loadAdminDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Admin Dashboard");
            stage.setScene(new Scene(root));
            stage.show();

            closeCurrentWindow();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Failed to load Admin Dashboard: " + e.getMessage());
        }
    }

    private void loadUserView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("UserView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("User View");
            stage.setScene(new Scene(root));
            stage.show();

            closeCurrentWindow();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Failed to load User View: " + e.getMessage());
        }
    }

    private void closeCurrentWindow() {
        Stage currentStage = (Stage) usernameField.getScene().getWindow();
        currentStage.close();
    }

    private void showAlert(AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Login");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }
}
