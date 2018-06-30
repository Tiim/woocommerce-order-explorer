package ch.scbirs.shop.orderexplorer;


import ch.scbirs.shop.orderexplorer.gui.Gui;

import java.io.IOException;

public class OrderExplorer {

    public static Env env = new Env();

    public static void main(String[] args) throws IOException {
        Gui.run(args);
    }

}
