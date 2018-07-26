package ch.scbirs.shop.orderexplorer.web.products;

import ch.scbirs.shop.orderexplorer.model.local.UserSettings;
import ch.scbirs.shop.orderexplorer.model.remote.products.ProductVariation;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.SteppedTask;
import ch.scbirs.shop.orderexplorer.web.LinkHeader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class SimpleProductFetcher implements SteppedTask {

    private static final Logger LOGGER = LogUtil.get();
    private final Queue<HttpUrl> queue = new ArrayDeque<>();
    private final OkHttpClient client = new OkHttpClient();
    private final List<ProductVariation> products;

    private int progress = 0;
    private int maxPages = 0;

    public SimpleProductFetcher(UserSettings settings) {
        products = new ArrayList<>();
        HttpUrl baseVariationUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(settings.getHost())
                .addPathSegments("wp-json/wc/v2/products")
                .addQueryParameter("consumer_key", settings.getConsumerKey())
                .addQueryParameter("consumer_secret", settings.getConsumerSecret())
                .build();
        queue.add(baseVariationUrl);
    }


    //TODO: Handle variable products if their prices are different.
    @Override
    public void doStep() throws IOException {
        handleProduct();
    }


    private void handleProduct() throws IOException {
        HttpUrl url = queue.poll();

        progress += 1;

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(response.body().byteStream());

        handleAnswer(jsonNode);

        String totalPages = response.header("x-wp-totalpages");
        if (totalPages != null) {
            maxPages = Integer.parseInt(totalPages);
        }

        LinkHeader link = new LinkHeader(response.headers("link"));
        link.get("next").ifPresent(l -> queue.add(HttpUrl.get(l)));
    }


    private void handleAnswer(JsonNode jsonNode) {
        jsonNode.forEach((pr) -> {
            ProductVariation product = handleNormalProduct(pr);
            products.add(product);
            LOGGER.info("Did load product " + product);
        });
    }

    private ProductVariation handleNormalProduct(JsonNode node) {
        LOGGER.trace("Handle Product " + node);
        return new ProductVariation(
                node.get("id").asInt(),
                node.get("name").asText(),
                node.get("sku").asText(),
                node.get("price").asText(),
                node.get("permalink").asText(),
                node.get("type").asText().equals("variable")
        );
    }

    public List<ProductVariation> getProducts() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public boolean isDone() {
        return queue.isEmpty();
    }

    @Override
    public int currentProgress() {
        return progress;
    }

    @Override
    public int maxProgress() {
        return maxPages;
    }

}
