package ch.scbirs.shop.orderexplorer.util;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.local.Status;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DataUtil {
    private static final Logger LOGGER = LogUtil.get();

    private static PseudoClass NOT_STARTED = PseudoClass.getPseudoClass("open");
    private static PseudoClass PAID = PseudoClass.getPseudoClass("paid");
    private static PseudoClass PAID_IN_STOCK = PseudoClass.getPseudoClass("paid_instock");
    private static PseudoClass IN_STOCK = PseudoClass.getPseudoClass("in_stock");
    private static PseudoClass DONE = PseudoClass.getPseudoClass("done");

    private static ArrayList<Pair<Status, PseudoClass>> CLASSES = new ArrayList<>();

    static {
        CLASSES.add(Pair.of(new Status(false, false, false), NOT_STARTED));
        CLASSES.add(Pair.of(new Status(false, true, false), PAID));
        CLASSES.add(Pair.of(new Status(true, true, false), PAID_IN_STOCK));
        CLASSES.add(Pair.of(new Status(true, false, false), IN_STOCK));
        CLASSES.add(Pair.of(new Status(true, true, true), DONE));
    }


    public static void setPseudoClass(Node n, OrderStatus status) {
        setPseudoClass(n, status == null ? null : status.toStatus());
    }

    public static void setPseudoClass(Node n, Status status) {
        for (Pair<Status, PseudoClass> p : CLASSES) {
            n.pseudoClassStateChanged(p.getRight(), p.getLeft().equals(status));
        }
    }

    public static String generateMailtoLink(Order o, ResourceBundle resources) {
        String subject = String.format(resources.getString("app.order.mail.Subject"), o.getId());
        StringBuilder body = new StringBuilder(String.format(resources.getString("app.order.mail.Body"),
                o.getFirstName(), o.getLastName()));
        body.append(String.format(resources.getString("app.order.mail.Body.Total"), o.getTotal()));
        for (Product p : o.getProducts()) {
            body.append(String.format(resources.getString("app.order.mail.Body.Product"),
                    p.getQuantity(), p.getName(), p.getPrice()));
            String meta = Util.formatMap(p.getMeta());
            if (!meta.isEmpty()) {
                body.append("* ").append(meta).append('\n');
            }
            body.append('\n');
        }
        Escaper escaper = UrlEscapers.urlFragmentEscaper();

        return String.format("mailto:%s?subject=%s&body=%s",
                o.getEmail(),
                escaper.escape(subject),
                escaper.escape(body.toString())
        );
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

        public Status toStatus() {
            return new Status(isInStock(), isPaid(), isDone());
        }
    }
}
