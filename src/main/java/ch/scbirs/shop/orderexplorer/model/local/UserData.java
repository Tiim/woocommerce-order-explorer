package ch.scbirs.shop.orderexplorer.model.local;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserData {

    private final Map<Integer, ProductData> productData;

    private UserData() {
        productData = null;
    }

    public UserData(Map<Integer, ProductData> productData) {
        this.productData = Collections.unmodifiableMap(new HashMap<>(productData));
    }

    public Map<Integer, ProductData> getProductData() {
        return productData;
    }
}
