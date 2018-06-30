package ch.scbirs.shop.orderexplorer.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;

    private final String note;
    private final String status;

    private final String total;
    private final List<Product> products;

    public Order(int id, String firstName, String lastName, String email, String status, String total, String note, List<Product> products) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
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
                Objects.equal(email, order.email) &&
                Objects.equal(note, order.note) &&
                Objects.equal(status, order.status) &&
                Objects.equal(total, order.total) &&
                Objects.equal(products, order.products);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, firstName, lastName, email, note, status, total, products);
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public String getStatus() {
        return status;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getTotal() {
        return total;
    }

    public List<Product> getProducts() {
        return products;
    }

    public static class Builder {
        private int id;
        private String firstName;
        private String lastName;
        private String email;
        private String status;
        private String total;
        private String note;
        private List<Product> products = new ArrayList<>();

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

        public Builder addProduct(Product product) {
            this.products.add(product);
            return this;
        }

        public Order build() {
            return new Order(id, firstName, lastName, email, status, total, note, products);
        }
    }
}
