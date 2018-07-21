package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.version.VersionUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ResourceBundle;

public class AboutDialog {
    private static final Logger LOGGER = LogUtil.get();
    private final ResourceBundle resources;

    @FXML
    private Label version;
    @FXML
    private Parent root;

    public AboutDialog(ResourceBundle resources) {
        this.resources = resources;
        load(resources);
    }

    private void load(ResourceBundle resources) {

        FXMLLoader loader = new FXMLLoader(GuiController.class.getResource("about_dialog.fxml"));
        loader.setResources(resources);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            LOGGER.error("Can't load about_dialog.fxml", e);
            throw new RuntimeException(e);
        }
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle(resources.getString("app.about.Title"));
        stage.getIcons().addAll(Icons.getIcons());
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles.css");

        stage.setScene(scene);

        stage.setResizable(false);
        stage.sizeToScene();

        stage.show();
    }

    @FXML
    private void initialize() {
        version.setText(VersionUtil.getVersion().toString());
    }
}
