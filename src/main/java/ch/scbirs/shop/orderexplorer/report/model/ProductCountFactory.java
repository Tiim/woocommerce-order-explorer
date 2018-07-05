package ch.scbirs.shop.orderexplorer.report.model;

import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductCountFactory {

    private final Data data;

    public ProductCountFactory(Data data) {

        this.data = data;
    }

    public List<ProductCount> build() {
        List<Product> products = data.getOrders()
                .stream()
                .flatMap(o -> o.getProducts().stream())
                .collect(Collectors.toList());

        List<ProductCount> counts = new ArrayList<>();

        for (Product p : products) {

            Optional<ProductCount> first = counts.stream().filter(pc -> pc.same(p)).findFirst();
            if (!first.isPresent()) {
                counts.add(new ProductCount(p));
            } else {
                first.get().inc(p.getQuantity());
            }
        }
        return counts;
    }

}
