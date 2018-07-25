package ch.scbirs.shop.orderexplorer.version;

import com.github.zafarkhaja.semver.Version;

public class VersionUtil {

    public static final Version DEV_VERSION = Version.valueOf("0.0.0-dev");

    public static Version getVersion() {
        String v = VersionUtil.class.getPackage().getSpecificationVersion();
        if (v == null || v.isEmpty()) {
            return DEV_VERSION;
        }
        return Version.valueOf(v);
    }

    public static Version fromString(String version) {
        if (version.startsWith("v")) {
            version = version.substring(1);
        }
        return Version.valueOf(version);
    }


}
