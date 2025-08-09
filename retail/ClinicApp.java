package retail;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ClinicApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Font.loadFont(getClass().getResource("Orbitron-Regular.ttf").toExternalForm(), 14);

        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(loader.load());

        
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        
        primaryStage.getIcons().add(new Image(getClass().getResource("logo.png").toExternalForm()));

        primaryStage.setTitle("Retail Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
