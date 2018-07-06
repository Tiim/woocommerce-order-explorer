package ch.scbirs.shop.orderexplorer.model.local;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class UserSettings {

    private final String host;
    private final String consumerKey;
    private final String consumerSecret;

    private UserSettings() {
        host = null;
        consumerKey = null;
        consumerSecret = null;
    }

    public UserSettings(String host, String consumerKey, String consumerSecret) {
        this.host = host;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public String getHost() {
        return host;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("host", host)
                .add("consumerKey", consumerKey)
                .add("consumerSecret", consumerSecret)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return Objects.equal(host, that.host) &&
                Objects.equal(consumerKey, that.consumerKey) &&
                Objects.equal(consumerSecret, that.consumerSecret);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(host, consumerKey, consumerSecret);
    }
}
