package ch.scbirs.shop.orderexplorer.model.local;

import ch.scbirs.shop.orderexplorer.model.remote.Product;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserData {

    private final Map<Integer, ProductData> productData;
    private final UserSettings userSettings;

    public UserData() {
        productData = Collections.emptyMap();
        userSettings = new UserSettings();
    }

    public UserData(Map<Integer, ProductData> productData, UserSettings userSettings) {
        Preconditions.checkNotNull(productData, "productdata can't be null. use Collections.emptyMap() instead");
        Preconditions.checkNotNull(userSettings, "userSettings can't be null");
        this.productData = Collections.unmodifiableMap(new HashMap<>(productData));
        this.userSettings = userSettings;
    }

    public ProductData getProductData(Product p) {
        ProductData pd = this.productData.get(p.getId());
        if (pd == null) {
            pd = new ProductData();
        }
        return pd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return Objects.equal(productData, userData.productData) &&
                Objects.equal(userSettings, userData.userSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productData, userSettings);
    }

    public Map<Integer, ProductData> getProductData() {
        return productData;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public UserData withProductData(Map<Integer, ProductData> productData){
        return new UserData(productData, userSettings);
    }

    public UserData withUserSettings(UserSettings userSettings) {
        return new UserData(productData, userSettings);
    }
}
