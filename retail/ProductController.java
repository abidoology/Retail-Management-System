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

public class ProductController {

    @FXML
    private TableView<Product> tblProducts;

    @FXML
    private TableColumn<Product, Integer> colId;

    @FXML
    private TableColumn<Product, String> colName;

    @FXML
    private TableColumn<Product, Double> colPrice;

    @FXML
    private TableColumn<Product, Integer> colQuantity;

    @FXML
    private TextField txtName, txtPrice, txtQuantity;

    @FXML
    private Button btnAdd, btnUpdate, btnDelete;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    private int selectedProductId = -1;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadProducts();

        tblProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                selectedProductId = newSelection.getId();
                txtName.setText(newSelection.getName());
                txtPrice.setText(String.valueOf(newSelection.getPrice()));
                txtQuantity.setText(String.valueOf(newSelection.getQuantity()));
                btnAdd.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });
    }

    private void loadProducts() {
        productList.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM products";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                productList.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tblProducts.setItems(productList);
    }

    @FXML
    void addProduct(ActionEvent event) {
        String name = txtName.getText();
        String priceText = txtPrice.getText();
        String quantityText = txtQuantity.getText();

        if(name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO products(name, price, quantity) VALUES (?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setDouble(2, price);
                pst.setInt(3, quantity);
                int rows = pst.executeUpdate();

                if(rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product added.");
                    clearFields();
                    loadProducts();
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Price and Quantity must be numeric.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateProduct(ActionEvent event) {
        if(selectedProductId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select a product to update.");
            return;
        }

        String name = txtName.getText();
        String priceText = txtPrice.getText();
        String quantityText = txtQuantity.getText();

        if(name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE products SET name = ?, price = ?, quantity = ? WHERE id = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setDouble(2, price);
                pst.setInt(3, quantity);
                pst.setInt(4, selectedProductId);
                int rows = pst.executeUpdate();

                if(rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated.");
                    clearFields();
                    loadProducts();
                    btnAdd.setDisable(false);
                    btnUpdate.setDisable(true);
                    btnDelete.setDisable(true);
                    selectedProductId = -1;
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Price and Quantity must be numeric.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void deleteProduct(ActionEvent event) {
        if(selectedProductId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Select a product to delete.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM products WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, selectedProductId);
            int rows = pst.executeUpdate();

            if(rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted.");
                clearFields();
                loadProducts();
                btnAdd.setDisable(false);
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
                selectedProductId = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtName.clear();
        txtPrice.clear();
        txtQuantity.clear();
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

    
    public static class Product {
        private int id;
        private String name;
        private double price;
        private int quantity;

        public Product(int id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
    }
}
