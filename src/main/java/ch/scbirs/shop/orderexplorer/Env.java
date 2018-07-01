package ch.scbirs.shop.orderexplorer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Env extends Properties {

    private static final Env INSTANCE = new Env();
    public final boolean debug;

    public Env() {
        try {
            load(new FileInputStream("DATA.env"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        debug = Boolean.parseBoolean(getProperty("debug"));
    }

    public static Env getInstance() {
        return INSTANCE;
    }



}
