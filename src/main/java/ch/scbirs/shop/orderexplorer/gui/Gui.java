package ch.scbirs.shop.orderexplorer.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Gui extends Application {

    public static void run(String[] args) {
        Gui.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("ScBirs Bestellungen");
        primaryStage.getIcons().add(new Image(Gui.class.getResourceAsStream("logo.png")));

        FXMLLoader loader = new FXMLLoader(Gui.class.getResource("gui.fxml"));
        Parent root = loader.load();
        GuiController controller = loader.getController();
        controller.setStage(primaryStage);

        Rectangle2D bounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(root, bounds.getWidth() * 2f / 3f, bounds.getHeight() * 2f / 3f);

        scene.getStylesheets().add("styles.css");

        primaryStage.setScene(scene);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
        primaryStage.show();

    }
}
