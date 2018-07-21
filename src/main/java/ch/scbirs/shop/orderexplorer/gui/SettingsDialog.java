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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ResourceBundle;

public class SettingsDialog extends Dialog<UserSettings> {
    private static final Logger LOGGER = LogUtil.get();
    @Nullable
    private final UserSettings oldSettings;

    @FXML
    private TextField host;
    @FXML
    private TextField key;
    @FXML
    private TextField secret;

    public SettingsDialog(@Nullable UserSettings settings, @Nonnull ResourceBundle bundle) {
        getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.APPLY);
        setTitle(bundle.getString("app.settings.Title"));
        this.oldSettings = settings;
        setResultConverter(b -> {
            if (b == ButtonType.APPLY) {
                UserSettings s = new UserSettings(host.getText().trim(), key.getText().trim(), secret.getText());
                if (s.isEmpty()) {
                    return null;
                }
                return s;
            } else {
                return null;
            }
        });
        try {
            FXMLLoader loader = new FXMLLoader(SettingsDialog.class.getResource("settings_dialog.fxml"));
            loader.setResources(bundle);
            loader.setController(this);
            Parent p = loader.load();
            getDialogPane().setContent(p);
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
