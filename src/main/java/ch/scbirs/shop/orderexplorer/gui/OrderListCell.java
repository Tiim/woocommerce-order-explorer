package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.model.Order;
import javafx.scene.control.ListCell;

public class OrderListCell extends ListCell<Order> {

    @Override
    protected void updateItem(Order item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText("");
        } else {
            setText(item.getFirstName() + " " + item.getLastName());
        }
    }
}
