package ch.scbirs.shop.orderexplorer.model;

import com.google.common.base.MoreObjects;

import java.util.Map;

public class Product {

    private final int quantity;
    private final String name;
    private final Map<String, String> meta;
    private final double price;
    private final String sku;

    public Product(int quantity, String name, Map<String, String> meta, double price, double totalPrice, String sku) {
        this.quantity = quantity;
        this.name = name;
        this.meta = meta;
        this.price = price;
        this.sku = sku;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("quantity", quantity)
                .add("name", name)
                .add("meta", meta)
                .add("price", price)
                .add("sku", sku)
                .toString();
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public double getPrice() {
        return price;
    }

    public String getSku() {
        return sku;
    }
}
