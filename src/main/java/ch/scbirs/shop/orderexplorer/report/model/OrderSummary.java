package ch.scbirs.shop.orderexplorer.report.model;

public class OrderSummary {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final double total;
    private final String email;

    public OrderSummary(int id, String firstName, String lastName, String email, double total) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.total = total;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getTotal() {
        return total;
    }

    public String getEmail() {
        return email;
    }
}
