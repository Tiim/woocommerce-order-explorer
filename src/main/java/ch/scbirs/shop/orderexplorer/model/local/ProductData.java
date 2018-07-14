package ch.scbirs.shop.orderexplorer.model.local;

import ch.scbirs.shop.orderexplorer.model.remote.Product;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class ProductData {

    @Nonnull
    private final Status status;

    public ProductData() {
        status = Status.OPEN;
    }

    public ProductData(@Nonnull Status status) {
        Preconditions.checkNotNull(status);
        this.status = status;
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }

    @Nonnull
    public ProductData withStatus(Status status) {
        return new ProductData(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductData that = (ProductData) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(status);
    }
}
