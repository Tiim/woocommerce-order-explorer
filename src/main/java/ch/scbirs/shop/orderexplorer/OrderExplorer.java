package ch.scbirs.shop.orderexplorer;

import java.io.IOException;

public class OrderExplorer {


    public static void main(String[] args) throws IOException {
        WebRequester wr = new WebRequester(new Env());

        wr.start();
    }

}
