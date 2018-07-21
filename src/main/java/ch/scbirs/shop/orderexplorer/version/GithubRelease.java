package ch.scbirs.shop.orderexplorer.version;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.zafarkhaja.semver.Version;

public class GithubRelease {

    private final String url;
    private final Version version;

    public GithubRelease(String url, Version version) {
        this.url = url;
        this.version = version;
    }

    public static GithubRelease fromJson(JsonNode json, String repo) {
        return new GithubRelease(
                json.get("html_url").asText(),
                VersionUtil.fromString(json.get("tag_name").asText())
        );
    }

    public Version getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }
}
