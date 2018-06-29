package ch.scbirs.shop.orderexplorer;


import ch.scbirs.shop.orderexplorer.gui.Gui;
import ch.scbirs.shop.orderexplorer.web.WebRequester;

import java.io.IOException;
import java.sql.SQLException;

public class OrderExplorer {

    public static Env env = new Env();

    public static void main(String[] args) throws IOException, SQLException {
        //Gui.run(args);

        WebRequester req = new WebRequester(env);
        while (!req.isDone()) {
            req.doRequest();
        }
        System.out.println(req.getOrders());
    }

}
