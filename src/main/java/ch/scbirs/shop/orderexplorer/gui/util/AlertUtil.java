package ch.scbirs.shop.orderexplorer.gui.util;

import javafx.scene.control.Alert;

public class AlertUtil {

    public static void showError(String message) {
        show(message, Alert.AlertType.ERROR, "Error");
    }

    public static void showWarning(String message) {
        show(message, Alert.AlertType.WARNING, "Warning");
    }

    public static void showInfo(String message) {
        show(message, Alert.AlertType.INFORMATION, "Information");
    }

    private static void show(String message, Alert.AlertType type, String title) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
