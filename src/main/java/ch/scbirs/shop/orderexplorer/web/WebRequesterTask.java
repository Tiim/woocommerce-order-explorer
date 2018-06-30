package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.model.Data;
import javafx.concurrent.Task;

public class WebRequesterTask extends Task<Data> {

    @Override
    protected Data call() throws Exception {
        OrderFetcher orderFetcher = new OrderFetcher(OrderExplorer.env);
        while (!orderFetcher.isDone() && !isCancelled()) {
            orderFetcher.doRequest();
            updateProgress(orderFetcher.currentProgress(), orderFetcher.maxProgress());
        }
        return new Data(orderFetcher.getOrders());
    }
}
