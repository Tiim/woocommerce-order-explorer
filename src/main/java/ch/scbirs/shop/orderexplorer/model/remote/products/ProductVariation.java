package ch.scbirs.shop.orderexplorer.model.remote.products;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ProductVariation {

    private final int id;
    @Nonnull
    private final String name;
    @Nonnull
    private final String sku;
    @Nonnull
    private final String price;
    @Nonnull
    private final String permalink;

    public ProductVariation() {
        id = 0;
        name = "";
        sku = "";
        price = "";
        permalink = "";
    }

    public ProductVariation(int id, @Nonnull String name, @Nonnull String sku, @Nonnull String price, @Nonnull String permalink) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.sku = Objects.requireNonNull(sku);
        this.price = Objects.requireNonNull(price);
        this.permalink = Objects.requireNonNull(permalink);
    }

    public int getId() {
        return id;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getSku() {
        return sku;
    }

    @Nonnull
    public String getPrice() {
        return price;
    }

    @Nonnull
    public String getPermalink() {
        return permalink;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("sku", sku)
                .add("price", price)
                .add("permalink", permalink)
                .toString();
    }
}
