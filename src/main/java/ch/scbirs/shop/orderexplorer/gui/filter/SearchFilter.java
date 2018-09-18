package ch.scbirs.shop.orderexplorer.gui.filter;

import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.util.Util;

import java.util.function.Function;

public class SearchFilter implements Function<Order, Boolean> {

    private final String search;

    public SearchFilter(String search) {

        this.search = search;
    }

    @Override
    public Boolean apply(Order order) {
        String data = gatherData(order);
        return data.toLowerCase().contains(search);
    }

    public String gatherData(Order order) {
        StringBuilder b = new StringBuilder();
        b.append(order.getFirstName()).append(" ");
        b.append(order.getLastName()).append(" ");
        b.append(order.getShippingFirstName()).append(" ");
        b.append(order.getShippingLastName()).append(" ");
        b.append(order.getEmail()).append(" ");
        b.append(order.getStatus()).append(" ");
        b.append(order.getNote()).append(" ");

        for (Product p : order.getProducts()) {
            b.append(p.getName()).append(" ");
            b.append(p.getSku()).append(" ");
            b.append(Util.formatMap(p.getMeta())).append(" ");
        }

        return b.toString();
    }
}
