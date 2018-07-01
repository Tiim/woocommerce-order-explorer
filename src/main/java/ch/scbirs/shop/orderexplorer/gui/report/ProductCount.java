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

    public void inc() {
        count += 1;
    }

    /**
     * Match same product variation only
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        Product p = null;
        if (o instanceof ProductCount) {
            p = ((ProductCount) o).getProduct();
        } else if (o instanceof Product) {
            p = (Product) o;
        } else {
            return false;
        }

        return p.getProductId() == product.getProductId() && p.getVariationId() == product.getVariationId();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("count", count)
                .add("product", product)
                .toString();
    }
}
