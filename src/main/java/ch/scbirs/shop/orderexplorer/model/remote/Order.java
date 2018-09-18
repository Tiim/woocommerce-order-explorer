package ch.scbirs.shop.orderexplorer.model.remote;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    private final int id;
    @Nonnull
    private final String firstName;
    @Nonnull
    private final String lastName;

    @Nonnull
    private final String shippingFirstName;
    @Nonnull
    private final String shippingLastName;

    @Nonnull
    private final String email;

    @Nonnull
    private final String note;
    @Nonnull
    private final String status;

    @Nonnull
    private final String total;
    @Nonnull
    private final List<Product> products;

    private Order() {
        id = 0;
        firstName = "";
        lastName = "";
        shippingFirstName = "";
        shippingLastName = "";
        email = "";
        note = "";
        status = "";
        total = "";
        products = Collections.emptyList();
    }

    public Order(int id, @Nonnull String firstName, @Nonnull String lastName, @Nonnull String shippingFirstName, @Nonnull String shippingLastName,
                 @Nonnull String email, @Nonnull String status, @Nonnull String total, @Nonnull String note, List<Product> products) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;

        this.shippingFirstName = shippingFirstName;
        this.shippingLastName = shippingLastName;

        this.email = email;
        this.status = status;

        this.total = total;
        this.note = note;
        this.products = Collections.unmodifiableList(new ArrayList<>(products));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("shippingFirstName", shippingFirstName)
                .add("shippingLastName", shippingLastName)
                .add("email", email)
                .add("note", note)
                .add("status", status)
                .add("total", total)
                .add("products", products)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id &&
                Objects.equal(firstName, order.firstName) &&
                Objects.equal(lastName, order.lastName) &&
                Objects.equal(shippingFirstName, order.shippingFirstName) &&
                Objects.equal(shippingLastName, order.shippingLastName) &&
                Objects.equal(email, order.email) &&
                Objects.equal(note, order.note) &&
                Objects.equal(status, order.status) &&
                Objects.equal(total, order.total) &&
                Objects.equal(products, order.products);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, firstName, lastName, shippingFirstName, shippingLastName, email, note, status, total, products);
    }

    public int getId() {
        return id;
    }

    @Nonnull
    public String getNote() {
        return note;
    }

    @Nonnull
    public String getStatus() {
        return status;
    }

    @Nonnull
    public String getFirstName() {
        return firstName;
    }

    @Nonnull
    public String getLastName() {
        return lastName;
    }

    @Nonnull
    public String getShippingFirstName() {
        return shippingFirstName;
    }

    @Nonnull
    public String getShippingLastName() {
        return shippingLastName;
    }

    @Nonnull
    public String getEmail() {
        return email;
    }

    @Nonnull
    public String getTotal() {
        return total;
    }

    @Nonnull
    public List<Product> getProducts() {
        return products;
    }

    public static class Builder {
        private int id;
        private String firstName;
        private String lastName;
        private String shippingFirstName;
        private String shippingLastName;
        private String email;
        private String status;
        private String total;
        private String note;
        private final List<Product> products = new ArrayList<>();

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setShippingFirstName(String shippingFirstName) {
            this.shippingFirstName = shippingFirstName;
            return this;
        }

        public Builder setShippingLastName(String shippingLastName) {
            this.shippingLastName = shippingLastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setTotal(String total) {
            this.total = total;
            return this;
        }

        public Builder setNote(String note) {
            this.note = note;
            return this;
        }

        public Builder addProduct(@Nonnull Product product) {
            Preconditions.checkNotNull(product);
            this.products.add(product);
            return this;
        }

        public Order build() {
            Preconditions.checkNotNull(firstName, "missing first name");
            Preconditions.checkNotNull(lastName, "missing last name");
            Preconditions.checkNotNull(shippingFirstName, "missing shipping first name");
            Preconditions.checkNotNull(shippingLastName, "missing shipping last name");
            Preconditions.checkNotNull(email, "missing email name");
            Preconditions.checkNotNull(status, "missing status name");
            Preconditions.checkNotNull(total, "missing total");
            Preconditions.checkNotNull(note, "missing note");

            return new Order(id, firstName, lastName, shippingFirstName, shippingLastName, email, status, total, note, products);
        }
    }
}
