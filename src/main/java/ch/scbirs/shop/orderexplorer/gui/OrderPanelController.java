package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.gui.hotkey.Hotkeys;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.local.ProductData;
import ch.scbirs.shop.orderexplorer.model.local.Status;
import ch.scbirs.shop.orderexplorer.model.local.UserData;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.util.DataUtil;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.Util;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import javafx.application.HostServices;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class OrderPanelController {
    private static final Logger LOGGER = LogUtil.get();
    private static final String HOTKEY_OPEN = "order.mark.Open";
    private static final String HOTKEY_IN_STOCK = "order.mark.InStock";
    private static final String HOTKEY_DELIVERED = "order.mark.Delivered";
    private final ChangeListener<Status> statusChangeListener = this::changed;

    private Order currentOrder;

    @FXML
    private ResourceBundle resources;

    @FXML
    private Label firstName;
    @FXML
    private Label status;
    @FXML
    private Label total;
    @FXML
    private Label email;
    @FXML
    private Label notes;
    @FXML
    private ListView<Product> list;
    @FXML
    private ComboBox<Status> statusDropdown;

    private ObjectProperty<Data> data;
    private HostServices hostServices;

    @FXML
    public void initialize() {
        list.setCellFactory(param -> new ProductListCell(data));
        statusDropdown.setItems(FXCollections.observableArrayList(Status.values()));
        statusDropdown.getSelectionModel().selectedItemProperty().addListener(statusChangeListener);

        Hotkeys hotkey = Hotkeys.getInstance();
        hotkey.keymap(HOTKEY_OPEN, new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHIFT_DOWN),
                () -> statusDropdown.getSelectionModel().select(Status.OPEN)
        );
        hotkey.keymap(HOTKEY_IN_STOCK, new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHIFT_DOWN),
                () -> statusDropdown.getSelectionModel().select(Status.IN_STOCK)
        );
        hotkey.keymap(HOTKEY_DELIVERED, new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHIFT_DOWN),
                () -> statusDropdown.getSelectionModel().select(Status.DELIVERED)
        );
    }


    private void changed(ObservableValue<? extends Status> observable, Status oldValue, Status newValue) {

        if (currentOrder == null) {
            //statusDropdown.getSelectionModel().clearSelection();
            return;
        }

        Data oldData = this.data.get();
        UserData oldUserData = oldData.getUserData();

        Map<Integer, ProductData> newProductDataMap = new HashMap<>(oldUserData.getProductData());
        for (Product p : currentOrder.getProducts()) {
            newProductDataMap.put(p.getId(), newProductDataMap.get(p.getId()).withStatus(newValue));
        }

        Data d = oldData.withUserData(oldData.getUserData().withProductData(newProductDataMap));

        data.set(d);
    }

    public void setCurrentOrder(Order order) {
        statusDropdown.getSelectionModel().selectedItemProperty().removeListener(statusChangeListener);
        currentOrder = order;
        if (order != null) {
            firstName.setText(order.getFirstName() + " " + order.getLastName());
            status.setText(order.getStatus());
            total.setText("CHF " + order.getTotal());
            email.setText(order.getEmail());
            notes.setText(order.getNote());

            statusDropdown.getSelectionModel().select(DataUtil.getOrderStatus(order, data.get()));

            list.setItems(FXCollections.observableArrayList(order.getProducts()));
        } else {
            firstName.setText("");
            status.setText("");
            total.setText("");
            email.setText("");

            notes.setText("");

            list.setItems(FXCollections.observableArrayList());
        }
        statusDropdown.getSelectionModel().selectedItemProperty().addListener(statusChangeListener);

    }

    public void setData(ObjectProperty<Data> data) {
        this.data = data;
    }

    @FXML
    private void sendMail(MouseEvent mouseEvent) {
        Order o = this.currentOrder;

        if (o == null) {
            return;
        }

        String subject = String.format(resources.getString("app.order.mail.Subject"), o.getId());
        String body = String.format(resources.getString("app.order.mail.Body"), o.getFirstName(), o.getLastName());
        body += String.format(resources.getString("app.order.mail.Body.Total"), o.getTotal());
        for (Product p : o.getProducts()) {
            body += String.format(resources.getString("app.order.mail.Body.Product"),
                    p.getQuantity(), p.getName(), p.getPrice());
            body += "* " + Util.formatMap(p.getMeta()) + "\n\n";
        }
        Escaper escaper = UrlEscapers.urlFragmentEscaper();

        String mailto = String.format("mailto:%s?subject=%s&body=%s",
                o.getEmail(),
                escaper.escape(subject),
                escaper.escape(body)
        );
        LOGGER.info("New mail: " + mailto);
        hostServices.showDocument(mailto);
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
