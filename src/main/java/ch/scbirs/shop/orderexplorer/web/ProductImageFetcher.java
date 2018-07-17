package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.Env;
import ch.scbirs.shop.orderexplorer.model.Data;
import ch.scbirs.shop.orderexplorer.model.local.UserSettings;
import ch.scbirs.shop.orderexplorer.model.remote.Product;
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
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class ProductImageFetcher implements SteppedTask {

    private static final Logger LOGGER = LogUtil.get();

    private static final int MAX_IMAGE_HEIGHT = 120;
    private static final int MAX_IMAGE_WIDTH = 120;

    private final Deque<Product> products;
    private final Path folder;
    private final OkHttpClient client = new OkHttpClient();
    private final int maxProgress;
    private final UserSettings settings;

    private final Map<String, String> output = new HashMap<>();

    private Product currentProduct = null;
    private String currentUrl = null;

    public ProductImageFetcher(List<Product> products, Path folder, UserSettings settings) {

        this.products = new ArrayDeque<>(products);
        this.folder = folder;
        maxProgress = products.size() * 2;
        this.settings = settings;
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

        String filename = FilenameUtils.getBaseName(currentUrl) + ".jpg";

        Path o = folder.resolve(filename);

        if (!Files.exists(o)) {
            HttpUrl url = HttpUrl.parse(currentUrl);
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {

                BufferedImage img = ImageIO.read(response.body().byteStream());
                BufferedImage scaled = scaleImage(img);

                ImageIO.write(scaled, "jpg", o.toFile());
            }
        } else {
            LOGGER.info("Skipping file " + o);
        }
        output.put(Data.getImageKeyForProduct(currentProduct), filename);

        currentUrl = null;
        currentProduct = null;
    }

    private BufferedImage scaleImage(BufferedImage img) {
        Image scaled = img.getScaledInstance(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT, Image.SCALE_SMOOTH);
        BufferedImage out = new BufferedImage(MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = out.createGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();

        return out;
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
        Env env = Env.getInstance();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(settings.getHost())
                .addPathSegments(String.format("wp-json/wc/v2/products/%d", product.getProductId(), product.getVariationId()))
                .addQueryParameter("consumer_key", settings.getConsumerKey())
                .addQueryParameter("consumer_secret", settings.getConsumerSecret())
                .build();

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(response.body().byteStream());

        return node.get("images").get(0).get("src").asText();
    }

    private String fetchNextUrlFromVariation(Product product) throws IOException {
        Env env = Env.getInstance();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(settings.getHost())
                .addPathSegments(String.format("wp-json/wc/v2/products/%d/variations/%d", product.getProductId(), product.getVariationId()))
                .addQueryParameter("consumer_key", settings.getConsumerKey())
                .addQueryParameter("consumer_secret", settings.getConsumerSecret())
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
