package ch.scbirs.shop.orderexplorer.model.local;

import com.google.common.base.Objects;

import javax.annotation.Nonnull;

public class ProductData {

    @Nonnull
    private final Status status;

    public ProductData() {
        status = new Status();
    }

    public ProductData(@Nonnull Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductData that = (ProductData) o;
        return Objects.equal(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(status);
    }

    @Nonnull
    public ProductData withStatus(Status status) {
        return new ProductData(status);
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }
}
