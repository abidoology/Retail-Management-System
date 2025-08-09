package retail;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private void openProduct(ActionEvent event) {
        loadFXML("Product.fxml");
    }

    @FXML
    private void openCustomer(ActionEvent event) {
        loadFXML("Customer.fxml");
    }

    @FXML
    private void openInventory(ActionEvent event) {
        loadFXML("Inventory.fxml");
    }
    
    
    @FXML
    private void openBillingView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BillingView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("All Billing Records");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
private void openBuyProducts(ActionEvent event) {
    try {
        Parent root = FXMLLoader.load(getClass().getResource("BuyProducts.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Buy Products");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @FXML
    private void handleLogout(ActionEvent event) {
        System.exit(0);
    }

    private void loadFXML(String fileName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fileName));
            Stage stage = new Stage();
            stage.setTitle(fileName.replace(".fxml", ""));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
