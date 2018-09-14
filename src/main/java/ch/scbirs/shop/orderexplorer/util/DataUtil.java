package ch.scbirs.shop.orderexplorer.util;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.local.Status;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import javafx.scene.Node;
import org.apache.logging.log4j.Logger;

public class DataUtil {
    private static final Logger LOGGER = LogUtil.get();

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
    public static Status getOrderStatus(Order order, Data data) {
        Status s = null;
        if (order != null && data != null) {
            for (Product p : order.getProducts()) {
                Status newS = data.getUserData().getProductData(p).getStatus();
                if (s == null) {
                    s = newS;
                } else if (!s.equals(newS)) {
                    return null;
                }
            }
        }
        return s;
    }
}
