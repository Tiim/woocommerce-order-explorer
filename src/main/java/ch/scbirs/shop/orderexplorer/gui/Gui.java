package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.backup.BackupProvider;
import ch.scbirs.shop.orderexplorer.gui.hotkey.Hotkeys;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Gui extends Application {

    public static void run(String[] args) {
        Gui.launch(args);
    }

    private Scene scene;
    @Override
    public void start(Stage primaryStage) throws Exception {

        Hotkeys.init(OrderExplorer.FOLDER.resolve("keymap.json"));
        BackupProvider.setRoot(OrderExplorer.FOLDER);

        ResourceBundle bundle = ResourceBundle.getBundle("lang.bundle");

        primaryStage.setTitle(bundle.getString("app.title"));
        primaryStage.getIcons().add(new Image(Gui.class.getResourceAsStream("logo.png")));

        FXMLLoader loader = new FXMLLoader(Gui.class.getResource("gui.fxml"));
        loader.setResources(bundle);
        Parent root = loader.load();
        GuiController controller = loader.getController();
        controller.setStage(primaryStage);
        controller.setHostServices(getHostServices());

        Rectangle2D bounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(root, bounds.getWidth() * 2f / 3f, bounds.getHeight() * 2f / 3f);

        scene.getStylesheets().add("styles.css");

        scene.setOnKeyPressed(event -> Hotkeys.getInstance().execute(event));

        primaryStage.setScene(scene);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
        primaryStage.show();
    }
}
