package ch.scbirs.shop.orderexplorer.model;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Person {

    private final String firstName;
    private final String lastName;
    private final String email;

    private final String total;
    private final List<Product> products;

    public Person(String firstName, String lastName, String email, String total, List<Product> products) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        this.total = total;
        this.products = Collections.unmodifiableList(new ArrayList<>(products));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("email", email)
                .add("total", total)
                .add("products", products)
                .toString();
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
        private String firstName;
        private String lastName;
        private String email;
        private String total;
        private List<Product> products = new ArrayList<>();

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

        public Builder setTotal(String total) {
            this.total = total;
            return this;
        }

        public Builder addProduct(Product product) {
            this.products.add(product);
            return this;
        }

        public Person build() {
            return new Person(firstName, lastName, email, total, products);
        }
    }
}
