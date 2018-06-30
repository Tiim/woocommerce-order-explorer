package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.web.WebRequesterTask;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class Gui extends Application {

    private Data data;

    private Stage primaryStage;

    @FXML
    private ListView list;

    public static void run(String[] args) {
        Gui.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        primaryStage.setTitle("ScBirs Bestellungen");
        primaryStage.getIcons().add(new Image(Gui.class.getResourceAsStream("logo.png")));

        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));

        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }

    @FXML
    private void onOpen(ActionEvent actionEvent) {

    }

    @FXML
    private void onSave(ActionEvent actionEvent) {

    }

    @FXML
    private void onReload(ActionEvent actionEvent) {

        Task<Data> task = new WebRequesterTask();

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "Operation in progress",
                ButtonType.CANCEL
        );
        alert.setTitle("Reloading data");
        alert.setHeaderText("Please wait..");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        alert.setGraphic(progressIndicator);

        progressIndicator.progressProperty().bind(task.progressProperty());

        alert.initOwner(primaryStage);
        alert.initModality(Modality.APPLICATION_MODAL);

        task.setOnSucceeded(event -> {
            alert.close();
            System.out.println(task.getValue());
        });
        task.setOnCancelled(event -> alert.close());
        task.setOnFailed(event -> {
            alert.close();
            Alert error = new ExceptionAlert(task.getException());
            error.show();
        });

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.CANCEL && task.isRunning()) {
            task.cancel();
        }
    }

    @FXML
    private void onExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    private void handleAboutAction(ActionEvent actionEvent) {

    }
}
