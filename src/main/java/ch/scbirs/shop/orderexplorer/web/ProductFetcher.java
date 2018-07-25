package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.model.local.UserSettings;
import ch.scbirs.shop.orderexplorer.model.remote.products.ProductVariation;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.SteppedTask;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class ProductFetcher implements SteppedTask {

    private static final Logger LOGGER = LogUtil.get();

    private final HttpUrl baseVariationUrl;
    private final Queue<HttpUrl> queue = new ArrayDeque<>();
    private final OkHttpClient client = new OkHttpClient();
    private final List<ProductVariation> products;

    private int progress = 0;
    private int maxPages = 0;

    public ProductFetcher(UserSettings settings) {
        products = new ArrayList<>();
        baseVariationUrl = new HttpUrl.Builder()
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
        jsonNode.forEach((product) -> {
            products.add(handleNormalProduct(product));
        });
    }

    private ProductVariation handleNormalProduct(JsonNode node) {
        return new ProductVariation(
                node.get("id").asInt(),
                node.get("name").asText(),
                node.get("sku").asText(),
                node.get("price").asText(),
                node.get("permalink").asText()
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
