package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.Order;
import ch.scbirs.shop.orderexplorer.web.WebRequesterTask;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class GuiController {

    private Data data;
    private Stage primaryStage;
    private OrderPanelController orderPanel;

    @FXML
    private ListView<Order> list;
    @FXML
    private AnchorPane detailPane;

    private void onNewData(Data data) {
        this.data = data;
        list.setItems(FXCollections.observableArrayList(data.getOrders()));
        orderPanel.setData(data);
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() throws IOException {
        list.setCellFactory(param -> new OrderListCell());

        list.getSelectionModel().selectedItemProperty().addListener((o, oldv, newv) -> orderPanel.setCurrentOrder(newv));

        FXMLLoader loader = new FXMLLoader(GuiController.class.getResource("order_panel.fxml"));
        Parent panel = loader.load();
        orderPanel = loader.getController();
        detailPane.getChildren().add(panel);
    }

    @FXML
    private void onOpen(ActionEvent actionEvent) throws IOException {
        Data data = Data.fromJsonFile(OrderExplorer.FOLDER.resolve("savefile.json"));
        onNewData(data);
    }

    @FXML
    private void onSave(ActionEvent actionEvent) throws IOException {
        Data.toJsonFile(OrderExplorer.FOLDER.resolve("savefile.json"), data);
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
            onNewData(task.getValue());
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
