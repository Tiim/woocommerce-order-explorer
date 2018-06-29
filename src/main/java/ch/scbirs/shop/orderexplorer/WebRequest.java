package ch.scbirs.shop.orderexplorer;

import ch.scbirs.shop.orderexplorer.web.LinkHeader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Properties;
import java.util.Queue;

public class WebRequest {


    private Queue<HttpUrl> queue = new ArrayDeque<>();
    private OkHttpClient client = new OkHttpClient();
    private final Properties env;

    public WebRequest(Env env) {
        this.env = env;
    }

    public void start() throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(env.getProperty("host"))
                .addPathSegments("wp-json/wc/v2/orders")
                .addQueryParameter("consumer_key", env.getProperty("consumer_key"))
                .addQueryParameter("consumer_secret", env.getProperty("consumer_secret"))
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
            System.out.print(billing.get("first_name").asText() + " ");
            System.out.print(billing.get("last_name").asText() + " ");
            System.out.print(billing.get("email").asText());

            System.out.println();
            order.get("line_items").forEach((product) -> {
                System.out.print("* " + product.get("quantity").asInt() + "x ");
                System.out.print(product.get("name").asText() + " ");
                System.out.print(product.get("sku").asText() + " ");
                System.out.print("CHF " + product.get("total").asText());
                product.get("meta_data").forEach((size) -> {
                    System.out.print(" " + size.get("value").asText());
                });
                System.out.println();
            });
            System.out.println( " = " +order.get("total").asText());
        });
    }

}
