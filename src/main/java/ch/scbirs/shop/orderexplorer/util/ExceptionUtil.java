package ch.scbirs.shop.orderexplorer.util;

import okhttp3.HttpUrl;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionUtil {

    public static String generateBugUrl(Throwable t) {
        String stacktrace = ExceptionUtils.getStackTrace(t);
        String title = ExceptionUtils.getRootCauseMessage(t);

        String desc = String.format("# Description\n\n\n--your description--\n# Exception\n\n```\n%s\n```\n/label ~\"auto\\-report\"\n", stacktrace);

        HttpUrl url = HttpUrl.parse("https://gitlab.com/Tiim/OrderExplorer/issues/new")
                .newBuilder()
                .addQueryParameter("issue[title]", title)
                .addQueryParameter("issue[description]", desc)
                .build();

        return url.toString();
    }

}
