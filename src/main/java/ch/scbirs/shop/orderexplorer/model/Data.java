package ch.scbirs.shop.orderexplorer.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Data {

    private final List<Order> orders;

    public Data(List<Order> orders) {
        this.orders = Collections.unmodifiableList(new ArrayList<>(orders));
    }

    public static Data fromJsonFile(Path file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Data data = mapper.readValue(file.toUri().toURL(), Data.class);
        return data;
    }

    public static void toJsonFile(Path file, Data data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file.toFile(), data);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orders", orders)
                .toString();
    }

    public List<Order> getOrders() {
        return orders;
    }
}
