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

public class InventoryController {

    @FXML
    private TableView<ProductController.Product> tblInventory;

    @FXML
    private TableColumn<ProductController.Product, Integer> colId;

    @FXML
    private TableColumn<ProductController.Product, String> colName;

    @FXML
    private TableColumn<ProductController.Product, Integer> colQuantity;

    private ObservableList<ProductController.Product> inventoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadInventory();
    }

    private void loadInventory() {
        inventoryList.clear();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, name, quantity FROM products";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                inventoryList.add(new ProductController.Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        0.0,
                        rs.getInt("quantity")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tblInventory.setItems(inventoryList);
    }
}
