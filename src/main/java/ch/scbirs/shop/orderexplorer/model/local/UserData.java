package ch.scbirs.shop.orderexplorer.model.local;

import ch.scbirs.shop.orderexplorer.model.remote.Product;
import com.google.common.base.Objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserData {

    private final Map<Integer, ProductData> productData;

    private UserData() {
        productData = new HashMap<>();
    }

    public UserData(Map<Integer, ProductData> productData) {
        this.productData = Collections.unmodifiableMap(new HashMap<>(productData));
    }

    public ProductData getProductData(Product p) {
        ProductData pd = this.productData.get(p.getId());
        if (pd == null) {
            pd = new ProductData();
        }
        return pd;
    }

    public Map<Integer, ProductData> getProductData() {
        return productData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return Objects.equal(productData, userData.productData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productData);
    }
}
