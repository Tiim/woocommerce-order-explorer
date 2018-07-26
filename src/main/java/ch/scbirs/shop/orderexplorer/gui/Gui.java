package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.backup.BackupProvider;
import ch.scbirs.shop.orderexplorer.gui.hotkey.Hotkeys;
import ch.scbirs.shop.orderexplorer.gui.util.ExceptionAlert;
import ch.scbirs.shop.orderexplorer.model.local.UserSettings;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.version.VersionUtil;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.ResourceBundle;

public class Gui extends Application {
    private static final Logger LOGGER = LogUtil.get();

    public static void run(String[] args) {
        Gui.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ExceptionAlert.setHostServices(getHostServices());

        Hotkeys.init(OrderExplorer.FOLDER.resolve("keymap.json"));
        BackupProvider.setRoot(OrderExplorer.FOLDER);

        ResourceBundle bundle = ResourceBundle.getBundle("lang.bundle");

        primaryStage.getIcons().addAll(Icons.getIcons());

        FXMLLoader loader = new FXMLLoader(Gui.class.getResource("gui.fxml"));
        loader.setResources(bundle);
        Parent root = loader.load();
        GuiController controller = loader.getController();

        UserSettings args = getUserSettings(getParameters().getNamed());

        primaryStage.titleProperty().bind(Bindings.format(bundle.getString("app.title"), VersionUtil.getVersion(),
                Bindings.createStringBinding(() -> controller.savedProperty().get() ? "" : "[*]", controller.savedProperty())));

        if (args != null) {
            LOGGER.info("Load user settings from commandline: " + args);
            controller.setUserSettings(args);
        }

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

    @Nullable
    private UserSettings getUserSettings(@Nonnull Map<String, String> args) {
        if (args.containsKey("host") && args.containsKey("ck") && args.containsKey("cs")) {
            return new UserSettings(args.get("host"), args.get("ck"), args.get("cs"));
        }
        return null;
    }
}
