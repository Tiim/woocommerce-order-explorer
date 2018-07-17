package ch.scbirs.shop.orderexplorer;

import ch.scbirs.shop.orderexplorer.util.LogUtil;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Env {
    private static final Logger LOGGER = LogUtil.get();
    private static final Env INSTANCE = new Env();
    public final boolean debug;

    public Env() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("DATA.env"));
        } catch (IOException e) {
            LOGGER.warn("Can't open DATA.env file");
        }
        debug = Boolean.parseBoolean(p.getProperty("debug"));
    }

    public static Env getInstance() {
        return INSTANCE;
    }


}
