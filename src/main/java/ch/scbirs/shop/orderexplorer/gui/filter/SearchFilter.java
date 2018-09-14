package ch.scbirs.shop.orderexplorer.gui.filter;

import ch.scbirs.shop.orderexplorer.model.remote.Order;

import java.util.function.Function;

public class SearchFilter implements Function<Order, Boolean> {

    private final String search;

    public SearchFilter(String search) {

        this.search = search;
    }

    @Override
    public Boolean apply(Order order) {
        StringBuilder b = new StringBuilder();
        b.append(order.getFirstName()).append(" ");
        b.append(order.getLastName()).append(" ");
        b.append(order.getEmail()).append(" ");
        b.append(order.getStatus()).append(" ");
        b.append(order.getNote()).append(" ");
        return b.toString().toLowerCase().contains(search);
    }
}
