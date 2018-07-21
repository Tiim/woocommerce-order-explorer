package ch.scbirs.shop.orderexplorer.util;

import java.util.Objects;

public class VersionUtil {

    public static String getVersion() {
        String v = VersionUtil.class.getPackage().getSpecificationVersion();
        return Objects.requireNonNullElse(v, "0.0.0");
    }

    public static String getFullVersion() {
        String v = VersionUtil.class.getPackage().getImplementationVersion();
        return Objects.requireNonNullElse(v, "0.0.0-DEV");
    }

}
