package ch.scbirs.shop.orderexplorer.model.local;

import com.google.common.base.Objects;

public class Status {

    private final boolean inStock;
    private final boolean paid;
    private final boolean done;

    public Status() {
        inStock = false;
        paid = false;
        done = false;
    }

    public Status(boolean inStock, boolean paid, boolean done) {
        this.done = done;
        if (done) {
            this.inStock = true;
            this.paid = true;
        } else {
            this.inStock = inStock;
            this.paid = paid;
        }
    }

    public Status withInStock(boolean inStock) {
        return new Status(inStock, this.paid, false);
    }

    public Status withPaid(boolean paid) {
        return new Status(this.inStock, paid, false);
    }

    public Status withDone(boolean done) {
        if (!done) {
            return new Status(inStock, paid, false);
        } else {
            return new Status(true, true, true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return inStock == status.inStock &&
                paid == status.paid &&
                done == status.done;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inStock, paid, done);
    }

    public boolean isInStock() {
        return inStock;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean isDone() {
        return done;
    }
}
