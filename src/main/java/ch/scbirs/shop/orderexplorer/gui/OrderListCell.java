package ch.scbirs.shop.orderexplorer.gui;

import ch.scbirs.shop.orderexplorer.Env;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.local.Status;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.util.DataUtil;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ListCell;

public class OrderListCell extends ListCell<Order> {

    private final ObjectProperty<Data> data;

    public OrderListCell(ObjectProperty<Data> data) {
        this.data = data;
    }

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
        Status s = DataUtil.getOrderStatus(item, data.get());
        DataUtil.setPseudoClass(this, s);
    }
}
