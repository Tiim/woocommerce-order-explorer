package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.model.Data;
import javafx.concurrent.Task;

public class WebRequesterTask extends Task<Data> {

    @Override
    protected Data call() throws Exception {
        WebRequester webRequester = new WebRequester(OrderExplorer.env);
        while (!webRequester.isDone() && !isCancelled()) {
            webRequester.doRequest();
            updateProgress(webRequester.currentProgress(), webRequester.maxProgress());
        }
        return new Data(webRequester.getOrders());
    }
}
