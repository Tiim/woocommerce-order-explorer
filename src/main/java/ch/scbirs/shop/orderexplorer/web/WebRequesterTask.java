package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.remote.Order;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
import ch.scbirs.shop.orderexplorer.model.remote.products.ProductVariation;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.web.products.ProductFetcher;
import javafx.concurrent.Task;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
        List<Order> orders = fetchOrders();
        // List all products that have been ordered
        Set<Product> allProducts = orders.stream()
                .flatMap(order -> order.getProducts().stream())
                .collect(Collectors.toSet());
        Map<String, String> images = fetchImages(allProducts);
        List<ProductVariation> variations = fetchProductVariations();
        updateMessage("Done");
        return prevData
                .withOrders(orders)
                .withImages(images)
                .withProductVariations(variations);
    }

    private List<ProductVariation> fetchProductVariations() throws Exception {
        updateMessage("Fetching all products");
        ProductFetcher fetcher = new ProductFetcher(prevData.getUserData().getUserSettings());
        while (!fetcher.isDone() && !isCancelled()) {
            fetcher.doStep();
            updateProgress(progress(fetcher.currentProgress(), fetcher.maxProgress(), 3, 3), 1);
        }
        System.out.println(fetcher.getProducts());
        return fetcher.getProducts();
    }

    private Map<String, String> fetchImages(Set<Product> allProducts) throws IOException {
        updateMessage("Fetching images");
        ProductImageFetcher fetcher = new ProductImageFetcher(new ArrayList<>(allProducts), OrderExplorer.FOLDER,
                prevData.getUserData().getUserSettings());
        while (!fetcher.isDone() && !isCancelled()) {
            fetcher.doStep();
            updateProgress(progress(fetcher.currentProgress(), fetcher.maxProgress(), 2, 3), 1);
        }
        return fetcher.getImages();
    }

    private List<Order> fetchOrders() throws IOException {
        updateMessage("Fetching orders");
        OrderFetcher fetcher = new OrderFetcher(prevData.getUserData().getUserSettings());
        while (!fetcher.isDone() && !isCancelled()) {
            fetcher.doStep();
            updateProgress(progress(fetcher.currentProgress(), fetcher.maxProgress(), 1, 3), 1);
        }
        return fetcher.getOrders();
    }

    private float progress(int current, int max, int step, int maxstep) {
        float currentProgress = (float) current / max;
        float stepProgress = (float) step / maxstep;
        float v = (currentProgress / maxstep) - (1f / maxstep) + stepProgress;
        LOGGER.trace("Progress " + v);
        return v;
    }
}
