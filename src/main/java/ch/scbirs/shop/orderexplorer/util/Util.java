package ch.scbirs.shop.orderexplorer.util;

import java.util.Map;

public class Util {

    public static String formatMap(Map<?, ?> map) {
        StringBuilder b = new StringBuilder();
        map.keySet().forEach(key -> {
            Object o = map.get(key);
            b.append(key).append(": ").append(o).append("\n");
        });
        if (b.length() > 0) {
            return b.substring(0, b.length() - 1);
        } else {
            return b.toString();
        }
    }

}
