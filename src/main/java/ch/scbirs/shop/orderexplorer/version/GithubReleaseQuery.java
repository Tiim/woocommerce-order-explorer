package ch.scbirs.shop.orderexplorer.version;

import ch.scbirs.shop.orderexplorer.util.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zafarkhaja.semver.Version;
import okhttp3.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GithubReleaseQuery {
    private static final Logger LOGGER = LogUtil.get();
    private static final String ACCEPT_HEADER = "application/vnd.github.v3+json";
    private static final String PS_RELEASES = "releases";
    private static final String PS_LATEST = "latest";
    @Nonnull
    static HttpUrl BASE_URL = Objects.requireNonNull(
            HttpUrl.parse("https://api.github.com/repos/")
    );
    private final OkHttpClient client = new OkHttpClient();
    private final String user;
    private final String repo;

    public GithubReleaseQuery(@Nonnull String user, @Nonnull String repo) {
        this.user = Objects.requireNonNull(user);
        this.repo = Objects.requireNonNull(repo);
    }

    @Nonnull
    public CompletableFuture<GithubRelease> getLatest() {
        LOGGER.info("Starting github release query");
        return CompletableFuture.supplyAsync(this::loadLatest);
    }

    public GithubRelease loadLatest() {
        try {
            LOGGER.info("Load release");
            HttpUrl url = makeUrl().addPathSegment(PS_LATEST).build();
            Request request = makeRequest(url);

            Response response = client.newCall(request).execute();
            LOGGER.info("Got response");
            ResponseBody body = checkResponse(response);

            ObjectMapper om = new ObjectMapper();
            JsonNode json = om.readTree(body.byteStream());
            GithubRelease githubRelease = GithubRelease.fromJson(json, user + "/" + repo);
            LOGGER.info("Found release " + githubRelease);
            return githubRelease;
        } catch (IOException e) {
            LOGGER.warn("Can't load release", e);
            throw new CompletionException(e);
        }
    }

    public CompletableFuture<Pair<Boolean, GithubRelease>> isNewerVersionAvailable(@Nonnull Version currentVersion) {
        Objects.requireNonNull(currentVersion);

        return getLatest().thenApply(ghr -> Pair.of(ghr.getVersion().greaterThan(currentVersion), ghr));
    }

    @Nonnull
    private ResponseBody checkResponse(@Nullable Response response) throws IOException {
        if (response == null) {
            throw new IOException("Unsuccessful request. No response");
        }
        if (!response.isSuccessful()) {
            throw new IOException("Unsuccessful request " +
                    response.header("Status") + " - " + response.message());
        }
        if (response.body() == null) {
            throw new IOException("Response has no body");
        }
        return response.body();
    }

    @Nonnull
    private HttpUrl.Builder makeUrl() {
        return BASE_URL
                .newBuilder()
                .addPathSegment(user)
                .addPathSegment(repo)
                .addPathSegment(PS_RELEASES);
    }

    @Nonnull
    private Request makeRequest(HttpUrl url) {
        return new Request.Builder()
                .url(url)
                .header("Accept", ACCEPT_HEADER)
                .build();
    }

}
