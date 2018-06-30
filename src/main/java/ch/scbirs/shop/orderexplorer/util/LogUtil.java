package ch.scbirs.shop.orderexplorer.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {

    @SuppressWarnings("ThrowableNotThrown")
    public static Logger get() {
        StackTraceElement[] elements = new Exception().getStackTrace();
        StackTraceElement element = elements[1];
        String className = element.getClassName();

        return LogManager.getLogger(className);
    }

}
