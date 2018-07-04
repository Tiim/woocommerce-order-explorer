package ch.scbirs.shop.orderexplorer.model.local;

import com.google.common.base.Objects;

public class ProductData {

    private final Status status;

    public ProductData() {
        status = Status.OPEN;
    }

    public ProductData(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
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
