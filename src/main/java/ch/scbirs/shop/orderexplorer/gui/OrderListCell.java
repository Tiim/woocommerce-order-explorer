package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.Env;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import javafx.scene.control.ListCell;

public class OrderListCell extends ListCell<Order> {

    @Override
    protected void updateItem(Order item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText("");
        } else {
            String string = item.getFirstName() + " " + item.getLastName();

            if (Env.getInstance().debug) {
                string += " (ID: " + item.getId() + ")";
            }

            setText(string);
        }
    }
}
