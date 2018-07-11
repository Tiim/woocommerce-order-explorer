package ch.scbirs.shop.orderexplorer.report.model;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderSummaryFactory {

    private final Data data;

    public OrderSummaryFactory(Data data) {

        this.data = data;
    }

    public List<OrderSummary> build() {
        List<OrderSummary> out = new ArrayList<>();
        for (Order o : data.getOrders()) {
            int id = o.getId();
            String firstName = o.getFirstName();
            String lastName = o.getLastName();
            String email = o.getEmail();

            double price = 0;

            for (Product p : o.getProducts()) {
                price += p.getPrice() * p.getQuantity();
            }

            out.add(new OrderSummary(id, firstName, lastName, email, price));
        }
        return out;
    }
}
