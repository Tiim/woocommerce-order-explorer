package ch.scbirs.shop.orderexplorer.web;

import ch.scbirs.shop.orderexplorer.Env;
import ch.scbirs.shop.orderexplorer.model.Order;
import ch.scbirs.shop.orderexplorer.model.Product;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class OrderFetcher {


    private Queue<HttpUrl> queue = new ArrayDeque<>();
    private OkHttpClient client = new OkHttpClient();
    private final Properties env;
    private List<Order> orders;

    private int currentPage = 0;
    private int maxPages = 0;

    public OrderFetcher(Env env) {
        this.env = env;

        orders = new ArrayList<>();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(env.getProperty("host"))
                .addPathSegments("wp-json/wc/v2/orders")
                .addQueryParameter("consumer_key", env.getProperty("consumer_key"))
                .addQueryParameter("consumer_secret", env.getProperty("consumer_secret"))
                .addQueryParameter("per_page", "1")
                .build();
        queue.add(url);
    }

    public void doRequest() throws IOException {
        HttpUrl url = queue.poll();

        currentPage += 1;

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        String totalPages = response.header("x-wp-totalpages");
        if (totalPages != null) {
            maxPages = Integer.parseInt(totalPages);
        }

        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(response.body().byteStream());

        handleAnswer(jsonNode);

        LinkHeader link = new LinkHeader(response.headers("link"));
        link.get("next").ifPresent(l -> queue.add(HttpUrl.get(l)));
    }

    private void handleAnswer(JsonNode jsonNode) {
        jsonNode.forEach((order) -> {
            JsonNode billing = order.get("billing");

            Order.Builder b = new Order.Builder()
                    .setId(order.get("id").asInt())
                    .setStatus(order.get("status").asText())
                    .setNote(order.get("customer_note").asText())
                    .setFirstName(billing.get("first_name").asText())
                    .setLastName(billing.get("last_name").asText())
                    .setEmail(billing.get("email").asText())
                    .setTotal(order.get("total").asText());

            order.get("line_items").forEach((product) -> {
                int quantity = product.get("quantity").asInt();
                String name = product.get("name").asText();
                String sku = product.get("sku").asText();
                double total = product.get("total").asDouble();
                double price = product.get("price").asDouble();

                Map<String, String> meta = new HashMap<>();

                product.get("meta_data").forEach((size) -> {
                    meta.put(size.get("key").asText(), size.get("value").asText());
                });
                b.addProduct(new Product(quantity, name, meta, price, total, sku));
            });
            handleOrder(b.build());
        });
    }

    private void handleOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public boolean isDone() {
        return queue.isEmpty();
    }

    public int currentProgress() {
        return currentPage;
    }

    public int maxProgress() {
        return maxPages;
    }

}
