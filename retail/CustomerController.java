package retail;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerController {

    @FXML
    private TableView<Customer> tblCustomers;

    @FXML
    private TableColumn<Customer, Integer> colId;

    @FXML
    private TableColumn<Customer, String> colName;

    @FXML
    private TableColumn<Customer, String> colEmail;

    @FXML
    private TableColumn<Customer, String> colPhone;

    @FXML
    private TextField txtName, txtEmail, txtPhone;

    @FXML
    private Button btnAdd, btnUpdate, btnDelete;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    private int selectedCustomerId = -1;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        loadCustomers();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                selectedCustomerId = newSelection.getId();
                txtName.setText(newSelection.getName());
                txtEmail.setText(newSelection.getEmail());
                txtPhone.setText(newSelection.getPhone());
                btnAdd.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
    }

    private void loadCustomers() {
        customerList.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                customerList.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tblCustomers.setItems(customerList);
    }

    @FXML
    void addCustomer(ActionEvent event) {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();

        if(name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO customers(name, email, phone) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            int rows = pst.executeUpdate();

            if(rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added.");
                clearFields();
                loadCustomers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateCustomer(ActionEvent event) {
        if(selectedCustomerId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select a customer to update.");
            return;
        }

        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();

        if(name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE customers SET name = ?, email = ?, phone = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, phone);
            pst.setInt(4, selectedCustomerId);
            int rows = pst.executeUpdate();

            if(rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated.");
                clearFields();
                loadCustomers();
                btnAdd.setDisable(false);
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
                selectedCustomerId = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deleteCustomer(ActionEvent event) {
        if(selectedCustomerId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select a customer to delete.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM customers WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, selectedCustomerId);
            int rows = pst.executeUpdate();

            if(rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer deleted.");
                clearFields();
                loadCustomers();
                btnAdd.setDisable(false);
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
                selectedCustomerId = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

    
    public static class Customer {
        private int id;
        private String name;
        private String email;
        private String phone;

        public Customer(int id, String name, String email, String phone) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
    }
}
