package retail;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignUpController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;

    
    @FXML private TextField txtName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;

    @FXML
    void handleSignUp(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();

        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
            || name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields.");
            return;
        }

        if(!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check if username already exists
            String checkSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if(rs.next()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
                return;
            }

            
            String insertUserSql = "INSERT INTO users (username, password, role, name, phone, email) VALUES (?, ?, 'user', ?, ?, ?)";
            PreparedStatement insertUserStmt = conn.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);
            insertUserStmt.setString(1, username);
            insertUserStmt.setString(2, password);
            insertUserStmt.setString(3, name);
            insertUserStmt.setString(4, phone);
            insertUserStmt.setString(5, email);

            int rows = insertUserStmt.executeUpdate();

            if(rows > 0) {
                
                ResultSet generatedKeys = insertUserStmt.getGeneratedKeys();
                int userId = 0;
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                }

                
                String insertCustomerSql = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";
                PreparedStatement insertCustomerStmt = conn.prepareStatement(insertCustomerSql);
                insertCustomerStmt.setString(1, name);
                insertCustomerStmt.setString(2, email);
                insertCustomerStmt.setString(3, phone);
                insertCustomerStmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully.");

                
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(loader.load()));
                loginStage.setTitle("Login");
                loginStage.show();

            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to register user.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        try {
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(loader.load()));
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open Login window.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }
}

