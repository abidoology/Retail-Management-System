package retail;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class BillingViewController {

    @FXML
    private TableView<BillingRecord> billingTable;

    @FXML
    private TableColumn<BillingRecord, Integer> colBillId;

    @FXML
    private TableColumn<BillingRecord, Integer> colCustomerId;

    @FXML
    private TableColumn<BillingRecord, String> colProduct;

    @FXML
    private TableColumn<BillingRecord, Integer> colQuantity;

    @FXML
    private TableColumn<BillingRecord, Double> colPrice;

    @FXML
    private TableColumn<BillingRecord, Double> colTotal;

    @FXML
    private TableColumn<BillingRecord, String> colCreatedAt;

    private ObservableList<BillingRecord> billingData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colBillId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        loadBillingDataFromDatabase();
    }

    private void loadBillingDataFromDatabase() {
        billingData.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM billing ORDER BY created_at DESC";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                BillingRecord record = new BillingRecord(
                        rs.getInt("bill_id"),
                        rs.getInt("customer_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total"),
                        rs.getString("created_at")
                );
                billingData.add(record);
            }
            billingTable.setItems(billingData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
