package ch.scbirs.shop.orderexplorer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OrderExplorer {



    public static void main(String[] args) throws IOException {
        Env env = new Env();
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(env.getProperty("host"))
                .addPathSegments("wp-json/wc/v2/orders")
                .addQueryParameter("consumer_key", env.getProperty("consumer_key"))
                .addQueryParameter("consumer_secret", env.getProperty("consumer_secret"))
                .build();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(response.body().byteStream());
        System.out.println(jsonNode);


    }

}
