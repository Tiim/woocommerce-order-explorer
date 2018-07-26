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

import java.util.*;
import java.util.stream.Collectors;

public class VariableProductFetcher implements SteppedTask {

    private static final Logger LOGGER = LogUtil.get();

    private final UserSettings settings;
    private final OkHttpClient client = new OkHttpClient();

    private final Queue<HttpUrl> pages = new ArrayDeque<>();
    private final Queue<ProductVariation> inputProducts = new ArrayDeque<>();
    private final List<ProductVariation> output = new ArrayList<>();

    private final HttpUrl baseVariationUrl;

    private ProductVariation lastProduct;

    private int progress = 0;
    private int maxProgress = 0;

    public VariableProductFetcher(UserSettings settings) {
        baseVariationUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(settings.getHost())
                .addPathSegments("wp-json/wc/v2/products")
                .addQueryParameter("consumer_key", settings.getConsumerKey())
                .addQueryParameter("consumer_secret", settings.getConsumerSecret())
                .build();
        this.settings = settings;
    }

    @Override
    public void doStep() throws Exception {
        HttpUrl url = null;
        if (pages.isEmpty()) {
            lastProduct = inputProducts.poll();
            url = baseVariationUrl.newBuilder()
                    .addPathSegment(String.valueOf(lastProduct.getId()))
                    .addPathSegment("variations")
                    .build();
        } else {
            url = pages.poll();
        }
        doStepWithUrl(url);
    }

    public void doStepWithUrl(HttpUrl url) throws Exception {

        progress += 1;

        LOGGER.info("Load variation from url " + url);

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(response.body().byteStream());

        handleAnswer(jsonNode);

        String totalPages = response.header("x-wp-totalpages");
        if (totalPages != null) {
            maxProgress += Integer.parseInt(totalPages) - 1;
        }

        LinkHeader link = new LinkHeader(response.headers("link"));
        link.get("next").ifPresent(l -> pages.add(HttpUrl.get(l)));
    }

    private void handleAnswer(JsonNode node) {
        node.forEach(v -> {
            ProductVariation product = handleVariableProduct(v, lastProduct);
            if (!product.getPrice().equalsIgnoreCase(lastProduct.getPrice())) {
                output.add(product);
                LOGGER.info("Add variation with different price than parent");
            } else if (!output.contains(lastProduct)) {
                output.add(lastProduct);
                LOGGER.info("Add parent product because they have the same price");
            } else {
                LOGGER.info("Skip product");
            }
        });
    }

    private ProductVariation handleVariableProduct(JsonNode node, ProductVariation pv) {
        LOGGER.trace("Handle Product " + node);
        return new ProductVariation(
                pv.getId(),
                pv.getName(),
                node.get("sku").asText(),
                node.get("price").asText(),
                node.get("permalink").asText(),
                true
        );
    }

    @Override
    public boolean isDone() {
        return inputProducts.isEmpty() && pages.isEmpty();
    }

    @Override
    public int currentProgress() {
        return progress;
    }

    @Override
    public int maxProgress() {
        return maxProgress;
    }

    public void setInputProducts(List<ProductVariation> inputProducts) {
        Map<Boolean, List<ProductVariation>> lists = inputProducts.stream()
                .collect(Collectors.partitioningBy(ProductVariation::isVariable));
        List<ProductVariation> variableProducts = lists.get(true);
        List<ProductVariation> otherProducts = lists.get(false);

        this.inputProducts.addAll(variableProducts);
        output.addAll(otherProducts);
        maxProgress = Math.max(this.inputProducts.size(), maxProgress);
    }

    public List<ProductVariation> getProducts() {
        return output;
    }
}
