package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.Util;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import javafx.application.HostServices;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.Logger;

public class OrderPanelController {
    private static final Logger LOGGER = LogUtil.get();
    private Order currentOrder;

    @FXML
    private Label firstName;
    @FXML
    private Label status;
    @FXML
    private Label total;
    @FXML
    private Label email;
    @FXML
    private ListView<Product> list;

    private ObjectProperty<Data> data;
    private HostServices hostServices;

    @FXML
    public void initialize() {
        list.setCellFactory(param -> new ProductListCell(data));
    }


    public void setCurrentOrder(Order order) {

        currentOrder = order;
        if (order != null) {
            firstName.setText(order.getFirstName() + " " + order.getLastName());
            status.setText(order.getStatus());
            total.setText("CHF " + order.getTotal());
            email.setText(order.getEmail());

            list.setItems(FXCollections.observableArrayList(order.getProducts()));
        } else {
            firstName.setText("");
            status.setText("");
            total.setText("");
            email.setText("");

            list.setItems(FXCollections.observableArrayList());
        }
    }

    public void setData(ObjectProperty<Data> data) {
        this.data = data;
    }

    @FXML
    private void sendMail(MouseEvent mouseEvent) {
        Order o = this.currentOrder;
        String subject = String.format("ScBirs - Bestellung #%d", o.getId());
        String body = String.format("Bestellung von %s %s%n", o.getFirstName(), o.getLastName());
        body += String.format("Total CHF %s%n%n%n", o.getTotal());
        for (Product p : o.getProducts()) {
            body += String.format("%dx %s - CHF %s%n", p.getQuantity(), p.getName(), p.getPrice());
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
