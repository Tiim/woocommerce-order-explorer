package ch.scbirs.shop.orderexplorer.model;

import ch.scbirs.shop.orderexplorer.model.local.UserData;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.model.remote.products.ProductVariation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Data {

    @Nonnull
    private final List<Order> orders;

    @Nonnull
    private final Map<String, String> images;

    @Nonnull
    private final List<ProductVariation> productVariations;

    @Nonnull
    private final UserData userData;

    public Data() {
        orders = Collections.emptyList();
        images = Collections.emptyMap();
        productVariations = Collections.emptyList();
        userData = new UserData();
    }

    public Data(@Nonnull List<Order> orders, @Nonnull Map<String, String> images,
                @Nonnull List<ProductVariation> productVariations, @Nonnull UserData userData) {
        Preconditions.checkNotNull(orders, "Orders array can't be null. Pass Collections.emptyList() instead");
        Preconditions.checkNotNull(images, "Images map can't be null. Pass Collections.emptyMap() instead");
        Preconditions.checkNotNull(userData, "UserData can't be null");
        this.orders = Collections.unmodifiableList(new ArrayList<>(orders));
        this.images = Collections.unmodifiableMap(new HashMap<>(images));
        this.productVariations = Collections.unmodifiableList(new ArrayList<>(productVariations));
        this.userData = userData;
    }

    public static Data fromJsonFile(@Nonnull Path file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file.toUri().toURL(), Data.class);
    }

    public static void toJsonFile(@Nonnull Path file, @Nonnull Data data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file.toFile(), data);
    }

    public static String getImageKeyForProduct(@Nonnull Product product) {
        return product.getProductId() + "-" + product.getVariationId();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orders", orders)
                .add("images", images)
                .add("userData", userData)
                .toString();
    }

    @Nonnull
    public List<Order> getOrders() {
        return orders;
    }

    @Nonnull
    public Map<String, String> getImages() {
        return images;
    }

    @Nullable
    public String getImage(Product product) {
        return images.get(getImageKeyForProduct(product));
    }

    @Nonnull
    public List<ProductVariation> getProductVariations() {
        return productVariations;
    }

    @Nonnull
    public UserData getUserData() {
        return userData;
    }

    @Nonnull
    public Data withOrders(List<Order> orders) {
        return new Data(orders, images, productVariations, userData);
    }

    @Nonnull
    public Data withImages(Map<String, String> images) {
        return new Data(orders, images, productVariations, userData);
    }

    @Nonnull
    public Data withProductVariations(List<ProductVariation> variations) {
        return new Data(orders, images, variations, userData);
    }

    @Nonnull
    public Data withUserData(UserData userData) {
        return new Data(orders, images, productVariations, userData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return Objects.equal(orders, data.orders) &&
                Objects.equal(images, data.images) &&
                Objects.equal(userData, data.userData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(orders, images, userData);
    }
}
