package ch.scbirs.shop.orderexplorer.model;

import ch.scbirs.shop.orderexplorer.model.local.UserData;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Data {

    private final List<Order> orders;

    private final Map<String, String> images;

    private final UserData userData;

    private Data() {
        orders = null;
        images = null;
        userData = null;
    }

    public Data(List<Order> orders, Map<String, String> images, UserData userData) {
        this.orders = Collections.unmodifiableList(new ArrayList<>(orders));
        this.images = Collections.unmodifiableMap(new HashMap<>(images));
        this.userData = userData;
    }

    public static Data fromJsonFile(Path file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file.toUri().toURL(), Data.class);
    }

    public static void toJsonFile(Path file, Data data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file.toFile(), data);
    }

    public static String getImageKeyForProduct(Product product) {
        return product.getProductId() + "-" + product.getVariationId();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orders", orders)
                .add("images", images)
                .toString();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public String getImage(Product product) {
        return images.get(getImageKeyForProduct(product));
    }

    public UserData getUserData() {
        return userData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return Objects.equal(orders, data.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orders);
    }
}
