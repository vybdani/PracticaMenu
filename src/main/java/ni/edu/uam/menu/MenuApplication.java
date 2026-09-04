package ni.edu.uam.menu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MenuApplication.class.getResource("producto-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Distribuidora El Güegüense - Gestión de productos");
        stage.setScene(scene);
        stage.show();
    }
}
