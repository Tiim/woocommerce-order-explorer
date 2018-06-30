package ch.scbirs.shop.orderexplorer.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Map;

public class Product {

    private final int quantity;
    private final String name;
    private final Map<String, String> meta;
    private final double price;
    private final String sku;

    private final int productId;
    private final int variationId;

    public Product(int quantity, String name, Map<String, String> meta, double price, String sku, int productId, int variationId) {
        this.quantity = quantity;
        this.name = name;
        this.meta = meta;
        this.price = price;
        this.sku = sku;
        this.productId = productId;
        this.variationId = variationId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("quantity", quantity)
                .add("name", name)
                .add("meta", meta)
                .add("price", price)
                .add("sku", sku)
                .add("productId", productId)
                .add("variationId", variationId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return quantity == product.quantity &&
                Double.compare(product.price, price) == 0 &&
                productId == product.productId &&
                variationId == product.variationId &&
                Objects.equal(name, product.name) &&
                Objects.equal(meta, product.meta) &&
                Objects.equal(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(quantity, name, meta, price, sku, productId, variationId);
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

    public int getProductId() {
        return productId;
    }

    public int getVariationId() {
        return variationId;
    }
}
