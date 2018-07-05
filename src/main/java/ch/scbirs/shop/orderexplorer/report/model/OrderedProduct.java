package ch.scbirs.shop.orderexplorer.report.model;

import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;

public class OrderedProduct {

    private final Order order;
    private final Product product;


    public OrderedProduct(Order order, Product product) {
        this.order = order;
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }
}
