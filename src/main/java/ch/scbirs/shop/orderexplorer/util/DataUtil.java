package ch.scbirs.shop.orderexplorer.util;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.local.Status;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class DataUtil {
    private static final Logger LOGGER = LogUtil.get();
    private static final ArrayList<Pair<Status, PseudoClass>> CLASSES = new ArrayList<>(Status.values().length);

    static {
        for (Status s : Status.values()) {
            CLASSES.add(new ImmutablePair<>(s, PseudoClass.getPseudoClass(s.toString().toLowerCase())));
        }
    }

    public static void setPseudoClass(Node n, Status status) {
        for (Pair<Status, PseudoClass> p : CLASSES) {
            n.pseudoClassStateChanged(p.getRight(), p.getLeft() == status);
        }
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
                } else if (s != newS) {
                    return null;
                }
            }
        }
        return s;
    }
}
