package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.model.local.UserSettings;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SettingsDialog extends Dialog<UserSettings> {
    private static final Logger LOGGER = LogUtil.get();
    private final UserSettings oldSettings;

    @FXML
    private TextField host;
    @FXML
    private TextField key;
    @FXML
    private TextField secret;

    public SettingsDialog(UserSettings settings) {
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);
        this.oldSettings = settings;
        setResultConverter(b ->
                b == ButtonType.APPLY ?
                        new UserSettings(host.getText().trim(), key.getText().trim(), secret.getText()) :
                        this.oldSettings);
        try {
            FXMLLoader loader = new FXMLLoader(SettingsDialog.class.getResource("settings_dialog.fxml"));
            loader.setController(this);
            Parent p = loader.load();
            getDialogPane().setContent(p);
//            getDialogPane().lookupButton(ButtonType.APPLY)
        } catch (IOException e) {
            LOGGER.error("Can't load settings dialog fxml", e);
        }

    }

    @FXML
    private void initialize() {
        if (oldSettings != null) {
            host.setText(oldSettings.getHost());
            key.setText(oldSettings.getConsumerKey());
            secret.setText(oldSettings.getConsumerSecret());
        }
    }
}
