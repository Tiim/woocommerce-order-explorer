package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.local.UserData;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import javafx.concurrent.Task;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WebRequesterTask extends Task<Data> {
    private static final Logger LOGGER = LogUtil.get();
    private final Data prevData;

    public WebRequesterTask(Data prevData) {

        this.prevData = prevData;
    }

    @Override
    protected Data call() throws Exception {
        OrderFetcher orderFetcher = new OrderFetcher(prevData.getUserData().getUserSettings());
        updateMessage("Fetching orders");
        while (!orderFetcher.isDone() && !isCancelled()) {
            orderFetcher.doStep();
            updateProgress(progress(orderFetcher.currentProgress(), orderFetcher.maxProgress(), 1, 2), 1);
        }
        List<Order> orders = orderFetcher.getOrders();
        Set<Product> allProducts = orders.stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.toSet());
        updateMessage("Fetching images");
        ProductImageFetcher imageFetcher = new ProductImageFetcher(new ArrayList<>(allProducts), OrderExplorer.FOLDER,
                prevData.getUserData().getUserSettings());
        while (!imageFetcher.isDone() && !isCancelled()) {
            imageFetcher.doStep();
            updateProgress(progress(imageFetcher.currentProgress(), imageFetcher.maxProgress(), 2, 2), 1);
        }
        Map<String, String> images = imageFetcher.getImages();
        updateMessage("Done");

        return prevData.withOrders(orders).withImages(images);
    }

    private final float progress(int current, int max, int step, int maxstep) {
        float currentProgress = (float) current / max;
        float stepProgress = (float) step / maxstep;
        float v = (currentProgress / maxstep) - (1f / maxstep) + stepProgress;
        LOGGER.trace("Progress " + v);
        return v;
    }
}
