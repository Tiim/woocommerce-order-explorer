package ch.scbirs.shop.orderexplorer.util;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.local.Status;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import javafx.scene.Node;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class DataUtil {
    private static final Logger LOGGER = LogUtil.get();

    public static void setPseudoClass(Node n, OrderStatus status) {
        //TODO: make pseudoclasses
    }

    public static void setPseudoClass(Node n, Status status) {
        //TODO: make pseudoclasses
    }

    /**
     * Returns the status of all products if all products have a single status.
     * If the status differs return null
     *
     * @param order
     * @param data
     * @return Status or Null
     */
    public static OrderStatus getOrderStatus(Order order, Data data) {
        OrderStatus s = null;
        if (order != null && data != null) {
            for (Product p : order.getProducts()) {
                Status status = data.getUserData().getProductData(p).getStatus();
                if (s == null) {
                    s = new OrderStatus(status.isPaid(), status.isInStock(), status.isDone());
                } else {
                    s = s.withStatus(status);
                }
            }
        }
        return s;
    }

    public static class OrderStatus {
        @Nullable
        private final Boolean paid;
        @Nullable
        private final Boolean inStock;
        @Nullable
        private final Boolean done;

        public OrderStatus(@Nullable Boolean paid, @Nullable Boolean inStock, @Nullable Boolean done) {
            this.paid = paid;
            this.inStock = inStock;
            this.done = done;
        }

        private OrderStatus withStatus(Status status) {
            return new OrderStatus(
                    (this.paid != null && this.paid == status.isPaid()) ? this.paid : null,
                    (this.inStock != null && this.inStock == status.isInStock()) ? this.inStock : null,
                    (this.done != null && this.done == status.isDone()) ? this.done : null
            );
        }

        public boolean isPaid() {
            return paid != null && paid;
        }

        public boolean isPaidIndetermiate() {
            return paid == null;
        }

        public boolean isInStock() {
            return inStock != null && inStock;
        }

        public boolean isInStockIndeterminate() {
            return inStock == null;
        }

        public boolean isDone() {
            return done != null && done;
        }

        public boolean isDoneIndeterminate() {
            return done == null;
        }
    }
}
