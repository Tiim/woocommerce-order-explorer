package ch.scbirs.shop.orderexplorer.gui.filter;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.util.DataUtil;
import javafx.beans.value.ObservableValue;

import java.util.function.Function;

public class InStockFilter implements Function<Order, Boolean> {

    private final ObservableValue<Data> data;

    public InStockFilter(ObservableValue<Data> data) {

        this.data = data;
    }

    @Override
    public Boolean apply(Order order) {
        DataUtil.OrderStatus status = DataUtil.getOrderStatus(order, data.getValue());
        return status.isInStockIndeterminate() || status.isInStock();
    }
}