package ch.scbirs.shop.orderexplorer.model.local;

public class ProductData {

    private final Status status;

    public ProductData() {
        status = Status.OPEN;
    }

    public ProductData(Status status) {
        this.status = status;
    }
}
