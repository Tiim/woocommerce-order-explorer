package ch.scbirs.shop.orderexplorer.gui.util;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TaskAlert<T> extends Alert {
    private final Task<T> task;

    public TaskAlert(Task<T> task, String title, String header, Stage stage) {
        super(AlertType.INFORMATION, "", ButtonType.CANCEL);
        this.task = task;
        setTitle(title);
        setHeaderText(header);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        setGraphic(progressIndicator);

        contentTextProperty().bind(task.messageProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());

        initOwner(stage);
        initModality(Modality.APPLICATION_MODAL);

        task.setOnCancelled(event -> close());
        task.setOnFailed(event -> {
            close();
            Alert error = new ExceptionAlert(task.getException());
            error.show();
        });
        setResultConverter(b -> {
            if (b == ButtonType.CANCEL && task.isRunning()) {
                task.cancel();
            }
            return b;
        });

    }
}
