package ch.scbirs.shop.orderexplorer.gui.report;

import ch.scbirs.shop.orderexplorer.model.remote.Product;
import com.google.common.base.MoreObjects;

public class ProductCount {

    private final Product product;
    private int count = 1;

    public ProductCount(Product product) {
        this.product = product;
    }

    public int getCount() {
        return count;
    }

    public Product getProduct() {
        return product;
    }

    public void inc(int amt) {
        count += amt;
    }

    public boolean same(Product p) {
        if (p.getProductId() != product.getProductId() || p.getVariationId() != product.getVariationId()) {
            return false;
        }
        return p.getMeta().equals(product.getMeta());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("count", count)
                .add("product", product)
                .toString();
    }
}
