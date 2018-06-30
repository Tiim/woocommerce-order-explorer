package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.Order;
import ch.scbirs.shop.orderexplorer.model.Product;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WebRequesterTask extends Task<Data> {

    @Override
    protected Data call() throws Exception {
        OrderFetcher orderFetcher = new OrderFetcher(OrderExplorer.env);
        while (!orderFetcher.isDone() && !isCancelled()) {
            orderFetcher.doStep();
            updateProgress(orderFetcher.currentProgress(), orderFetcher.maxProgress());
        }
        List<Order> orders = orderFetcher.getOrders();
        Set<Product> allProducts = orders.stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.toSet());

        ProductImageFetcher imageFetcher = new ProductImageFetcher(OrderExplorer.env, new ArrayList<>(allProducts), OrderExplorer.FOLDER);
        while (!imageFetcher.isDone() && !isCancelled()) {
            imageFetcher.doStep();
            updateProgress(imageFetcher.currentProgress(), imageFetcher.maxProgress());
        }
        Map<String, String> images = imageFetcher.getImages();
        return new Data(orders, images);
    }
}
