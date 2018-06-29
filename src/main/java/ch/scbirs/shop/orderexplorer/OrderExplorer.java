package ch.scbirs.shop.orderexplorer;

import java.io.IOException;

public class OrderExplorer {


    public static void main(String[] args) throws IOException {
        WebRequest wr = new WebRequest(new Env());

        wr.start();
    }

}
