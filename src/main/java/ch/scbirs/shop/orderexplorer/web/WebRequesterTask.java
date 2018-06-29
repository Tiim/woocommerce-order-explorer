package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.OrderExplorer;
import ch.scbirs.shop.orderexplorer.model.Data;
import javafx.concurrent.Task;

public class WebRequesterTask extends Task<Data> {

    @Override
    protected Data call() throws Exception {
        WebRequester webRequester = new WebRequester(OrderExplorer.env);
        System.out.println("start");
        while (!webRequester.isDone() && !isCancelled()) {
            try {
                webRequester.doRequest();
                updateProgress(webRequester.currentProgress(), webRequester.maxProgress());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("end");
        return new Data(webRequester.getOrders());
    }
}
