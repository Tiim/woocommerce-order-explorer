package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.model.local.UserSettings;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Logger;

public class CheckConnectionTask extends Task<Boolean> {

    private static final Logger LOGGER = LogUtil.get();

    private final UserSettings settings;
    private final OkHttpClient client = new OkHttpClient();

    public CheckConnectionTask(UserSettings settings) {

        this.settings = settings;
    }

    @Override
    protected Boolean call() {
        try {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host(settings.getHost())
                    .addPathSegments("wp-json/wc/v2/system_status")
                    .addQueryParameter("consumer_key", settings.getConsumerKey())
                    .addQueryParameter("consumer_secret", settings.getConsumerSecret())
                    .build();

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            ObjectMapper om = new ObjectMapper();
            if (response.body() == null) {
                return false;
            }
            JsonNode json = om.readTree(response.body().byteStream());

            String version = json.get("environment").get("wp_version").asText();
            LOGGER.info("Remote running Wordpress " + version);

            return true;
        } catch (Exception e) {
            LOGGER.warn("Failed to connect to the remote: " + e.getMessage());
            return false;
        }
    }
}
