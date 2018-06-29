package ch.scbirs.shop.orderexplorer;

import ch.scbirs.shop.orderexplorer.model.Person;
import ch.scbirs.shop.orderexplorer.model.Product;
import ch.scbirs.shop.orderexplorer.web.LinkHeader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class WebRequester {


    private Queue<HttpUrl> queue = new ArrayDeque<>();
    private OkHttpClient client = new OkHttpClient();
    private final Properties env;

    public WebRequester(Env env) {
        this.env = env;
    }

    public void start() throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(env.getProperty("host"))
                .addPathSegments("wp-json/wc/v2/orders")
                .addQueryParameter("consumer_key", env.getProperty("consumer_key"))
                .addQueryParameter("consumer_secret", env.getProperty("consumer_secret"))
                //TODO: remove this, its only for testing purposes
                .addQueryParameter("per_page", "1")
                .build();
        queue.add(url);

        while (!queue.isEmpty()) {
            doRequest();
        }
    }

    private void doRequest() throws IOException {
        HttpUrl url = queue.poll();

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(response.body().byteStream());

        handleAnswer(jsonNode);

        LinkHeader link = new LinkHeader(response.headers("link"));
        link.get("next").ifPresent(l -> queue.add(HttpUrl.get(l)));
    }

    private void handleAnswer(JsonNode jsonNode) {
        jsonNode.forEach((order) -> {
            JsonNode billing = order.get("billing");

            Person.Builder b = new Person.Builder()
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

    private void handleOrder(Person person) {
        System.out.println(person);
    }

}
