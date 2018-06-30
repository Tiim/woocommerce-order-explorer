package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.Env;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.Product;
import ch.scbirs.shop.orderexplorer.util.LogUtil;
import ch.scbirs.shop.orderexplorer.util.SteppedTask;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ProductImageFetcher implements SteppedTask {

    private static final Logger LOGGER = LogUtil.get();

    private final Env env;
    private final Deque<Product> products;
    private final Path folder;
    private final OkHttpClient client = new OkHttpClient();
    private final int maxProgress;

    private final Map<String, String> output = new HashMap<>();

    private Product currentProduct = null;
    private String currentUrl = null;

    public ProductImageFetcher(Env env, List<Product> products, Path folder) {

        this.env = env;
        this.products = new ArrayDeque<>(products);
        this.folder = folder;
        maxProgress = products.size() * 2;
    }

    @Override
    public void doStep() throws IOException {
        if (currentUrl == null) {
            fetchNextUrl();
        } else {
            downloadImage();
        }
    }

    private void downloadImage() throws IOException {

        String filename = FilenameUtils.getName(currentUrl);

        Path o = folder.resolve(filename);

        if (!Files.exists(o)) {
            HttpUrl url = HttpUrl.get(new URL(currentUrl));
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            IOUtils.copy(response.body().byteStream(), Files.newOutputStream(o));
        } else {
            LOGGER.info("Skipping file " + o);
        }
        output.put(Data.getImageKeyForProduct(currentProduct), filename);

        currentUrl = null;
        currentProduct = null;
    }

    private void fetchNextUrl() throws IOException {
        Product product = products.poll();
        Preconditions.checkNotNull(product);

        String url = fetchNextUrlFromVariation(product);

        if (url.contains("placeholder")) {
            url = fetchNextUrlFromProduct(product);
        }

        currentUrl = url;
        currentProduct = product;

        LOGGER.info("Found url url");
    }

    private String fetchNextUrlFromProduct(Product product) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(env.getProperty("host"))
                .addPathSegments(String.format("wp-json/wc/v2/products/%d", product.getProductId(), product.getVariationId()))
                .addQueryParameter("consumer_key", env.getProperty("consumer_key"))
                .addQueryParameter("consumer_secret", env.getProperty("consumer_secret"))
                .build();

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(response.body().byteStream());

        return node.get("images").get(0).get("src").asText();
    }

    private String fetchNextUrlFromVariation(Product product) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(env.getProperty("host"))
                .addPathSegments(String.format("wp-json/wc/v2/products/%d/variations/%d", product.getProductId(), product.getVariationId()))
                .addQueryParameter("consumer_key", env.getProperty("consumer_key"))
                .addQueryParameter("consumer_secret", env.getProperty("consumer_secret"))
                .build();

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(response.body().byteStream());

        return node.get("image").get("src").asText();
    }


    @Override
    public boolean isDone() {
        return products.isEmpty() && currentUrl == null;
    }

    @Override
    public int currentProgress() {
        return maxProgress - (products.size() * 2) - (currentUrl == null ? 0 : 1);
    }

    @Override
    public int maxProgress() {
        return maxProgress;
    }


    public Map<String, String> getImages() {
        return output;
    }
}
