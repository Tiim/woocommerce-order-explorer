package ch.scbirs.shop.orderexplorer.gui.filter;

import ch.scbirs.shop.orderexplorer.model.remote.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Filter {

    private final List<Order> orders;
    private final ObservableList<Order> filteredOutput;

    private final Map<String, Function<Order, Boolean>> filters;

    public Filter(List<Order> orders) {
        this.orders = orders;
        this.filteredOutput = FXCollections.observableArrayList(orders);

        filters = new HashMap<>();
    }

    public Filter clone(List<Order> orders) {
        Filter filter = new Filter(orders);
        for (String key : filters.keySet()) {
            filter.addFilter(key, filters.get(key));
        }
        return filter;
    }


    public void addFilter(String name, Function<Order, Boolean> filter) {
        filters.put(name, filter);
        update();
    }

    public void toggleFilter(String name, Function<Order, Boolean> filter) {
        if (filters.containsKey(name)) {
            removeFilter(name);
        } else {
            addFilter(name, filter);
        }
    }

    public void removeFilter(String name) {
        filters.remove(name);
        update();
    }

    private void update() {
        int index = 0;
        for (Order o : orders) {
            boolean include = true;
            for (Function<Order, Boolean> filter : filters.values()) {
                include = filter.apply(o);
                if (!include) {
                    break;
                }
            }
            if (include) {
                if (!filteredOutput.contains(o)) {
                    filteredOutput.add(index, o);
                }
                index += 1;
            } else {
                filteredOutput.remove(o);
            }
        }
    }

    public ObservableList<Order> getFilteredOutput() {
        return filteredOutput;
    }
}
