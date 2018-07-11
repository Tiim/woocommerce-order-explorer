package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.backup.BackupProvider;
import ch.scbirs.shop.orderexplorer.gui.hotkey.HotkeySettings;
import ch.scbirs.shop.orderexplorer.gui.hotkey.Hotkeys;
import ch.scbirs.shop.orderexplorer.gui.report.MoneyReport;
import ch.scbirs.shop.orderexplorer.gui.report.OrderReport;
import ch.scbirs.shop.orderexplorer.gui.report.OverviewReport;
import ch.scbirs.shop.orderexplorer.gui.report.ReportScreen;
import ch.scbirs.shop.orderexplorer.gui.util.AlertUtil;
import ch.scbirs.shop.orderexplorer.gui.util.ExceptionAlert;
import ch.scbirs.shop.orderexplorer.gui.util.TaskAlert;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.local.UserData;
import ch.scbirs.shop.orderexplorer.model.local.UserSettings;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.report.FullReport;
import ch.scbirs.shop.orderexplorer.util.BindingUtil;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.web.CheckConnectionTask;
import ch.scbirs.shop.orderexplorer.web.WebRequesterTask;
import javafx.application.HostServices;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GuiController {

    private static final Logger LOGGER = LogUtil.get();
    private static final String HOTKEY_RELOAD = "data.Reload";
    private static final String HOTKEY_REPORT_ORDER = "report.export.OrderReport";
    private static final String HOTKEY_REPORT_OVERVIEW = "report.export.OverviewReport";
    private static final String HOTKEY_REPORT_FULL = "report.export.FullReport";

    private ObjectProperty<Data> data = new SimpleObjectProperty<>();
    private Stage primaryStage;
    private OrderPanelController orderPanel;
    @FXML
    private ResourceBundle resources;

    @FXML
    private TextField search;
    @FXML
    private Menu recentBackup;
    @FXML
    private ListView<Order> list;
    @FXML
    private AnchorPane detailPane;
    private HostServices hostServices;


    public GuiController() {
        data.addListener(this::onNewData);
    }

    private void onNewData(ObservableValue<? extends Data> o, Data oldData, Data data) {
        LOGGER.info("Data value has changed " + data);
        int idx = list.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            idx = 0;
        }
        list.setItems(FXCollections.observableArrayList(data.getOrders()));
        if (list.getItems().size() > idx) {
            list.getSelectionModel().select(idx);
        }
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() throws IOException {

        ReportScreen.setResources(resources);

        list.setCellFactory(param -> new OrderListCell(data));

        list.getSelectionModel().selectedItemProperty().addListener((o, oldv, newv) -> orderPanel.setCurrentOrder(newv));

        FXMLLoader loader = new FXMLLoader(GuiController.class.getResource("order_panel.fxml"));
        loader.setResources(resources);
        Parent panel = loader.load();
        orderPanel = loader.getController();
        orderPanel.setData(this.data);
        detailPane.getChildren().add(panel);

        if (Files.exists(OrderExplorer.SETTINGS_FILE)) {
            try {
                data.setValue(Data.fromJsonFile(OrderExplorer.SETTINGS_FILE));
            } catch (Exception e) {
                LOGGER.warn("Failed to open json file on startup", e);
            }
        }

        Hotkeys hotkeys = Hotkeys.getInstance();
        hotkeys.keymap(HOTKEY_RELOAD, KeyCode.F5, this::onReload);
        hotkeys.keymap(HOTKEY_REPORT_ORDER, KeyCode.F1, this::generateReportOrder);
        hotkeys.keymap(HOTKEY_REPORT_OVERVIEW, KeyCode.F2, this::generateReportOverview);
        hotkeys.keymap(HOTKEY_REPORT_FULL, KeyCode.F3, this::generateReportFull);


        ObservableList<String> backups = BackupProvider.backups();
        ObservableList<MenuItem> items = recentBackup.getItems();
        EventHandler<ActionEvent> actionEventHandle = event -> {
            String filename = ((MenuItem) event.getSource()).getText();
            ExceptionAlert.doTry(() -> {
                        data.set(BackupProvider.loadBackup(filename));
                    }
            );
        };
        BindingUtil.mapContent(items, backups, MenuItem::new,
                m -> m.setOnAction(actionEventHandle), m -> m.setOnAction(null)
        );
    }

    @FXML
    private void onOpen(ActionEvent actionEvent) throws IOException {
        Data d = Data.fromJsonFile(OrderExplorer.SETTINGS_FILE);
        data.setValue(d);
    }

    @FXML
    private void onSave(ActionEvent actionEvent) throws IOException {
        if (data.get() != null) {
            try {
                BackupProvider.nextBackup(data.get());
            } catch (IOException e) {
                LOGGER.warn("Can't make backup", e);
            }
            Data.toJsonFile(OrderExplorer.SETTINGS_FILE, data.get());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(resources.getString("app.dialog.save.NoData"));
            alert.show();
        }
    }

    @FXML
    private void onSettingsDialog() {
        SettingsDialog sd = null;
        Data old = this.data.get();
        if (old == null || old.getUserData() == null || old.getUserData().getUserSettings() == null) {
            sd = new SettingsDialog(null, resources);
        } else {
            sd = new SettingsDialog(old.getUserData().getUserSettings(), resources);
        }
        Optional<UserSettings> newsettings = sd.showAndWait();
        if (newsettings.isPresent()) {
            Data d;
            if (old == null) {
                d = new Data(null, null, new UserData(null, newsettings.get()));
            } else {
                d = new Data(old.getOrders(), old.getImages(), new UserData(old.getUserData().getProductData(), newsettings.get()));
            }

            Task<Boolean> task = new CheckConnectionTask(newsettings.get());
            TaskAlert<Boolean> alert = new TaskAlert<>(task, resources.getString("app.dialog.conncheck.Title"),
                    resources.getString("app.dialog.conncheck.Header"), primaryStage);

            task.setOnSucceeded(e -> {
                alert.close();
                if (!task.getValue()) {
                    AlertUtil.showError(resources.getString("app.dialog.conncheck.Error"), primaryStage);
                } else {
                    data.set(d);
                }
            });

            Thread t = new Thread(task);
            t.start();
            alert.show();
        }
    }

    @FXML
    private void onCheckConnection() {
        if (data.get() == null || data.get().getUserData() == null || data.get().getUserData().getUserSettings() == null) {
            AlertUtil.showError(resources.getString("app.dialog.conncheck.NoSettings"), primaryStage);
            return;
        }
        Task<Boolean> task = new CheckConnectionTask(data.get().getUserData().getUserSettings());
        TaskAlert<Boolean> alert = new TaskAlert<>(task, resources.getString("app.dialog.conncheck.Title"),
                resources.getString("app.dialog.conncheck.Header"), primaryStage);

        task.setOnSucceeded(e -> {
            alert.close();
            if (!task.getValue()) {
                AlertUtil.showError(resources.getString("app.dialog.conncheck.Error"), primaryStage);
            } else {
                AlertUtil.showInfo(resources.getString("app.dialog.conncheck.Success"), primaryStage);
            }
        });

        Thread t = new Thread(task);
        t.start();
        alert.show();
    }

    @FXML
    private void onReload() {

        if (data.get() != null) {
            try {
                BackupProvider.nextBackup(data.get());
            } catch (IOException e) {
                LOGGER.warn("Can't make backup", e);
            }
        }

        if (data.get() == null || data.get().getUserData() == null || data.get().getUserData().getUserSettings() == null) {
            onSettingsDialog();
        }

        Task<Data> task = new WebRequesterTask(data.get());

        Alert alert = new TaskAlert<>(task, resources.getString("app.dialog.loading.Title"),
                resources.getString("app.dialog.loading.Header"), primaryStage);

        task.setOnSucceeded(event -> {
            alert.close();
            data.setValue(task.getValue());
            ExceptionAlert.doTry(() -> Data.toJsonFile(OrderExplorer.SETTINGS_FILE, data.get()));
        });

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        alert.show();
    }

    @FXML
    private void onBackup(ActionEvent actionEvent) {
        if (data != null) {
            ExceptionAlert.doTry(() -> BackupProvider.nextBackup(data.get()));
        } else {
            AlertUtil.showWarning(resources.getString("app.dialog.backup.NoData"), primaryStage);
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
    private void generateReportOrder() throws IOException {
        ReportScreen rs = new ReportScreen(new OrderReport(data.get()));
        rs.show();
    }

    @FXML
    private void generateReportOverview() throws IOException {
        ReportScreen rs = new ReportScreen(new OverviewReport(data.get()));
        rs.show();
    }

    @FXML
    private void generateReportMoney() throws IOException {
        ReportScreen rs = new ReportScreen(new MoneyReport(data.get()));
        rs.show();
    }

    @FXML
    private void generateReportFull() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(resources.getString("app.dialog.report.full.filechooser.Title"));
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel File", "*.xls"));
        File selectedFile = chooser.showSaveDialog(primaryStage);
        if (selectedFile != null) {
            ExceptionAlert.doTry(() -> {
                new FullReport(data.get()).save(selectedFile.toPath());
            });
        }
    }

    @FXML
    private void onHotkeyDialog() {
        HotkeySettings hs = new HotkeySettings(primaryStage, resources, Hotkeys.getInstance());
        hs.show();
    }
}
