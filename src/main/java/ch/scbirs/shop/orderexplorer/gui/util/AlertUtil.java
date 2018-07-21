package ch.scbirs.shop.orderexplorer.gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

public class AlertUtil {

    public static void showError(String message, Stage stage) {
        show(message, stage, Alert.AlertType.ERROR, "Error");
    }

    public static void showWarning(String message, Stage stage) {
        show(message, stage, Alert.AlertType.WARNING, "Warning");
    }

    public static void showInfo(String message, Stage stage) {
        show(message, stage, Alert.AlertType.INFORMATION, "Information");
    }

    private static void show(String message, Stage stage, Alert.AlertType type, String title) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.initOwner(stage);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public static Alert setDefaultButton(Alert alert, ButtonType defBtn) {
        DialogPane pane = alert.getDialogPane();
        for (ButtonType t : alert.getButtonTypes())
            ((Button) pane.lookupButton(t)).setDefaultButton(t == defBtn);
        return alert;
    }
}
