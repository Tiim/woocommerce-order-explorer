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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class OrderPanelController {
    private static final Logger LOGGER = LogUtil.get();
    private static final String HOTKEY_PAID = "order.toggle.Paid";
    private static final String HOTKEY_IN_STOCK = "order.toggle.InStock";
    private static final String HOTKEY_DONE = "order.toggle.Done";

    private final EventHandler<ActionEvent> changeListener = this::changed;

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
    private CheckBox isPaid;
    @FXML
    private CheckBox isInStock;
    @FXML
    private CheckBox isDone;


    private ObjectProperty<Data> data;
    private HostServices hostServices;

    @FXML
    public void initialize() {
        list.setCellFactory(param -> new ProductListCell(data));


        isPaid.setOnAction(changeListener);
        isInStock.setOnAction(changeListener);
        isDone.setOnAction(changeListener);

        Hotkeys hotkey = Hotkeys.getInstance();
        hotkey.keymap(HOTKEY_PAID, new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHIFT_DOWN),
                () -> isPaid.fire()
        );
        hotkey.keymap(HOTKEY_IN_STOCK, new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHIFT_DOWN),
                () -> isInStock.fire()
        );
        hotkey.keymap(HOTKEY_DONE, new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHIFT_DOWN),
                () -> isDone.fire()
        );
    }


    private void changed(ActionEvent event) {

        if (currentOrder == null) {
            return;
        }

        Data oldData = this.data.get();
        UserData oldUserData = oldData.getUserData();

        DataUtil.OrderStatus newStatus = new DataUtil.OrderStatus(
                isPaid.isIndeterminate() ? null : isPaid.isSelected(),
                isInStock.isIndeterminate() ? null : isInStock.isSelected(),
                isDone.isIndeterminate() ? null : isDone.isSelected()
        );


        Map<Integer, ProductData> newProductDataMap = new HashMap<>(oldUserData.getProductData());
        for (Product p : currentOrder.getProducts()) {
            ProductData productData = newProductDataMap.get(p.getId());
            if (productData == null) {
                productData = new ProductData();
            }
            Status oldStatus = productData.getStatus();

            if (!newStatus.isPaidIndetermiate()) {
                oldStatus = oldStatus.withPaid(newStatus.isPaid());
            }
            if (!newStatus.isInStockIndeterminate()) {
                oldStatus = oldStatus.withInStock(newStatus.isInStock());
            }
            if (!newStatus.isDoneIndeterminate()) {
                oldStatus = oldStatus.withDone(newStatus.isDone());
            }

            newProductDataMap.put(p.getId(), productData.withStatus(oldStatus));
        }

        Data d = oldData.withUserData(oldData.getUserData().withProductData(newProductDataMap));
        data.set(d);

        updateStatus(DataUtil.getOrderStatus(currentOrder, d));
    }

    private void updateStatus(@Nonnull DataUtil.OrderStatus status) {
        isPaid.setOnAction(null);
        isInStock.setOnAction(null);
        isDone.setOnAction(null);

        isPaid.setSelected(status.isPaid());
        isPaid.setIndeterminate(status.isPaidIndetermiate());
        isInStock.setSelected(status.isInStock());
        isInStock.setIndeterminate(status.isInStockIndeterminate());
        isDone.setSelected(status.isDone());
        isDone.setIndeterminate(status.isDoneIndeterminate());

        isPaid.setOnAction(changeListener);
        isInStock.setOnAction(changeListener);
        isDone.setOnAction(changeListener);
    }


    public void setCurrentOrder(Order order) {


        currentOrder = order;
        if (order != null) {
            firstName.setText(order.getFirstName() + " " + order.getLastName());
            status.setText(order.getStatus());
            total.setText("CHF " + order.getTotal());
            email.setText(order.getEmail());
            notes.setText(order.getNote());

            updateStatus(DataUtil.getOrderStatus(order, data.get()));

            list.setItems(FXCollections.observableArrayList(order.getProducts()));
        } else {
            firstName.setText("");
            status.setText("");
            total.setText("");
            email.setText("");

            notes.setText("");

            updateStatus(new DataUtil.OrderStatus(null, null, null));

            list.setItems(FXCollections.observableArrayList());
        }
    }

    public void setData(ObjectProperty<Data> data) {
        this.data = data;
    }

    @FXML
    private void sendMail() {
        Order o = this.currentOrder;

        if (o == null) {
            return;
        }

        String subject = String.format(resources.getString("app.order.mail.Subject"), o.getId());
        StringBuilder body = new StringBuilder(String.format(resources.getString("app.order.mail.Body"),
                o.getFirstName(), o.getLastName()));
        body.append(String.format(resources.getString("app.order.mail.Body.Total"), o.getTotal()));
        for (Product p : o.getProducts()) {
            body.append(String.format(resources.getString("app.order.mail.Body.Product"),
                    p.getQuantity(), p.getName(), p.getPrice()));
            body.append("* ").append(Util.formatMap(p.getMeta())).append("\n\n");
        }
        Escaper escaper = UrlEscapers.urlFragmentEscaper();

        String mailto = String.format("mailto:%s?subject=%s&body=%s",
                o.getEmail(),
                escaper.escape(subject),
                escaper.escape(body.toString())
        );
        LOGGER.info("New mail: " + mailto);
        hostServices.showDocument(mailto);
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
