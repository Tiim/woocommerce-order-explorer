package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.version.VersionUtil;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import javafx.application.HostServices;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ResourceBundle;

public class AboutDialog {
    private static final Logger LOGGER = LogUtil.get();
    private final ResourceBundle resources;
    private final HostServices hostServices;
    private final String html;

    @FXML
    private Label version;
    @FXML
    private Parent root;
    @FXML
    private WebView credits;

    public AboutDialog(ResourceBundle resources, HostServices hostServices) {
        this.resources = resources;
        this.hostServices = hostServices;
        String html;
        try {
            html = Resources.toString(AboutDialog.class.getResource("credits.html"), Charsets.UTF_8);
        } catch (IOException e) {
            html = "<i>Failed to load about</i>";
        }
        this.html = html;
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
        WebEngine engine = credits.getEngine();
        engine.loadContent(html);

        engine.locationProperty().addListener(this::urlChanged);
    }

    private void urlChanged(ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
        if (!newValue.isEmpty()) {
            hostServices.showDocument(newValue);
            credits.getEngine().loadContent(html);
        }
    }
}
