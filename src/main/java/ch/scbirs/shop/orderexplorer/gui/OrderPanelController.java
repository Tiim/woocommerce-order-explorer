package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.Order;
import ch.scbirs.shop.orderexplorer.model.Product;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class OrderPanelController {

    private Order currentOrder;

    @FXML
    private Label firstName;
    @FXML
    private Label lastName;
    @FXML
    private Label status;
    @FXML
    private Label total;
    @FXML
    private Label email;
    @FXML
    private ListView<Product> list;

    private Data data;

    @FXML
    public void initialize() {
        list.setCellFactory(param -> new ProductListCell(this));
    }


    public void setCurrentOrder(Order order) {

        currentOrder = order;

        firstName.setText(order.getFirstName());
        lastName.setText(order.getLastName());
        status.setText(order.getStatus());
        total.setText(order.getTotal());
        email.setText(order.getEmail());

        list.setItems(FXCollections.observableArrayList(order.getProducts()));

    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
