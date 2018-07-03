package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.gui.report.OrderReport;
import ch.scbirs.shop.orderexplorer.gui.report.OverviewReport;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.web.WebRequesterTask;
import javafx.application.HostServices;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuiController {

    private static final Logger LOGGER = LogUtil.get();

    private ObjectProperty<Data> data = new SimpleObjectProperty<>();
    private Stage primaryStage;
    private OrderPanelController orderPanel;

    @FXML
    private TextField search;
    @FXML
    private ListView<Order> list;
    @FXML
    private AnchorPane detailPane;
    private HostServices hostServices;

    private void onNewData(Data data) {
        this.data.setValue(data);
        list.setItems(FXCollections.observableArrayList(data.getOrders()));
        list.getSelectionModel().select(0);
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
        orderPanel.setData(this.data);
        detailPane.getChildren().add(panel);

        if (Files.exists(OrderExplorer.SETTINGS_FILE)) {
            try {
                onNewData(Data.fromJsonFile(OrderExplorer.SETTINGS_FILE));
            } catch (Exception e) {
                LOGGER.warn("Failed to open json file on startup", e);
            }
        }
    }

    @FXML
    private void onOpen(ActionEvent actionEvent) throws IOException {
        Data data = Data.fromJsonFile(OrderExplorer.SETTINGS_FILE);
        onNewData(data);
    }

    @FXML
    private void onSave(ActionEvent actionEvent) throws IOException {
        Data.toJsonFile(OrderExplorer.SETTINGS_FILE, data.get());
    }

    @FXML
    private void onReload(ActionEvent actionEvent) {

        Task<Data> task = new WebRequesterTask(data.get());

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "",
                ButtonType.CANCEL
        );
        alert.setTitle("Reloading data");
        alert.setHeaderText("Please wait..");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        alert.setGraphic(progressIndicator);

        alert.contentTextProperty().bind(task.messageProperty());
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

    @FXML
    private void onSearch(KeyEvent keyEvent) {
        String s = search.getText().toLowerCase();
        List<Order> filtered = data.get().getOrders().stream()
                .filter(o -> (o.getFirstName() + " " + o.getLastName()).toLowerCase().contains(s))
                .collect(Collectors.toList());
        list.setItems(FXCollections.observableArrayList(filtered));
        list.getSelectionModel().select(0);
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
        orderPanel.setHostServices(hostServices);
    }

    @FXML
    private void generateReportOrder(ActionEvent actionEvent) throws IOException {
        OrderReport report = new OrderReport(data.get());
        report.show();
    }

    @FXML
    private void generateReportOverview(ActionEvent actionEvent) throws IOException {
        OverviewReport report = new OverviewReport(data.get());
        report.show();
    }
}
