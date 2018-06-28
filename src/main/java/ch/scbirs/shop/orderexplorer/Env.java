package ch.scbirs.shop.orderexplorer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Env extends Properties {

    public Env() {
        try {
            load(new FileInputStream("DATA.env"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
