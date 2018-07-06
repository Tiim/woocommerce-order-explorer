package ch.scbirs.shop.orderexplorer.gui.util;

import javafx.scene.control.Alert;

public class AlertUtil {

    public static void showError(String message) {
        show(message, Alert.AlertType.ERROR);
    }

    public static void showInfo(String message) {
        show(message, Alert.AlertType.INFORMATION);
    }

    private static void show(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
