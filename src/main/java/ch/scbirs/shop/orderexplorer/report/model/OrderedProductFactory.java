package ch.scbirs.shop.orderexplorer.report.model;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderedProductFactory {

    private final Data data;

    public OrderedProductFactory(Data data) {

        this.data = data;
    }

    public List<OrderedProduct> build() {
        List<OrderedProduct> out = new ArrayList<>();
        for (Order o : data.getOrders()) {
            for (Product p : o.getProducts()) {
                out.add(new OrderedProduct(o, p));
            }
        }
        return out;
    }
}
