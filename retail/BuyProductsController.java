package retail;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;

public class BuyProductsController {

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtCustomerEmail;

    @FXML
    private TextField txtCustomerPhone;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> colId;

    @FXML
    private TableColumn<Product, String> colName;

    @FXML
    private TableColumn<Product, Double> colPrice;

    @FXML
    private TableColumn<Product, Integer> colQuantity;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/retail_db", "root", "WJ28@krhps");
    }

    @FXML
    public void initialize() {
    
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colPrice.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        colQuantity.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());

        loadProducts();
    }

    private void loadProducts() {
        productList.clear();
        String query = "SELECT * FROM products WHERE quantity > 0";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                productList.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
            productTable.setItems(productList);

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Failed to load products: " + e.getMessage());
        }
    }

    @FXML
    private void handleBuyProduct(ActionEvent event) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        String name = txtCustomerName.getText().trim();
        String email = txtCustomerEmail.getText().trim();
        String phone = txtCustomerPhone.getText().trim();

        if (selectedProduct == null) {
            showAlert(AlertType.WARNING, "No Product Selected", "Please select a product to buy.");
            return;
        }
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert(AlertType.WARNING, "Missing Customer Info", "Please fill all customer details.");
            return;
        }

        try (Connection conn = connect()) {
            conn.setAutoCommit(false); 

            
            String checkCustomerSql = "SELECT id FROM customers WHERE email = ? OR phone = ?";
            PreparedStatement checkCustomerStmt = conn.prepareStatement(checkCustomerSql);
            checkCustomerStmt.setString(1, email);
            checkCustomerStmt.setString(2, phone);
            ResultSet rs = checkCustomerStmt.executeQuery();

            int customerId;
            if (rs.next()) {
                customerId = rs.getInt("id");
            } else {
                
                String insertCustomerSql = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";
                PreparedStatement insertCustomerStmt = conn.prepareStatement(insertCustomerSql, Statement.RETURN_GENERATED_KEYS);
                insertCustomerStmt.setString(1, name);
                insertCustomerStmt.setString(2, email);
                insertCustomerStmt.setString(3, phone);
                int affectedRows = insertCustomerStmt.executeUpdate();

                if (affectedRows == 0) {
                    conn.rollback();
                    showAlert(AlertType.ERROR, "Error", "Failed to add customer.");
                    return;
                }
                try (ResultSet generatedKeys = insertCustomerStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customerId = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        showAlert(AlertType.ERROR, "Error", "Failed to get customer ID.");
                        return;
                    }
                }
            }

            
            String insertBillingSql = "INSERT INTO billing (customer_id, product_name, quantity, price, total) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertBillingStmt = conn.prepareStatement(insertBillingSql);
            insertBillingStmt.setInt(1, customerId);
            insertBillingStmt.setString(2, selectedProduct.getName());
            insertBillingStmt.setInt(3, 1); 
            insertBillingStmt.setDouble(4, selectedProduct.getPrice());
            insertBillingStmt.setDouble(5, selectedProduct.getPrice());
            int billingRows = insertBillingStmt.executeUpdate();

            if (billingRows == 0) {
                conn.rollback();
                showAlert(AlertType.ERROR, "Error", "Failed to save billing.");
                return;
            }

            
            String updateProductSql = "UPDATE products SET quantity = quantity - 1 WHERE id = ?";
            PreparedStatement updateProductStmt = conn.prepareStatement(updateProductSql);
            updateProductStmt.setInt(1, selectedProduct.getId());
            updateProductStmt.executeUpdate();

            conn.commit();

            showAlert(AlertType.INFORMATION, "Success", "Product purchased successfully!");
            loadProducts(); 
            clearFields();

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Error during purchase: " + e.getMessage());
        }
    }

    private void clearFields() {
        txtCustomerName.clear();
        txtCustomerEmail.clear();
        txtCustomerPhone.clear();
    }

    private void showAlert(AlertType alertType, String title, String message) {
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
