package ch.scbirs.shop.orderexplorer.model.local;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class UserSettings {

    @Nonnull
    private final String host;
    @Nonnull
    private final String consumerKey;
    @Nonnull
    private final String consumerSecret;

    public UserSettings() {
        host = "";
        consumerKey = "";
        consumerSecret = "";
    }

    public UserSettings(@Nonnull String host, @Nonnull String consumerKey, @Nonnull String consumerSecret) {
        Preconditions.checkNotNull(host);
        Preconditions.checkNotNull(consumerKey);
        Preconditions.checkNotNull(consumerSecret);
        this.host = host;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    @Nonnull
    public String getHost() {
        return host;
    }

    @Nonnull
    public String getConsumerKey() {
        return consumerKey;
    }

    @Nonnull
    public String getConsumerSecret() {
        return consumerSecret;
    }

    public UserSettings withHost(String host) {
        return new UserSettings(host, consumerKey, consumerSecret);
    }

    @Nonnull
    public UserSettings withConsumerKey(String consumerKey) {
        return new UserSettings(host, consumerKey, consumerSecret);
    }

    @Nonnull
    public UserSettings withConsumerSecret(String consumerSecret) {
        return new UserSettings(host, consumerKey, consumerSecret);
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

    @JsonIgnore
    public boolean isEmpty() {
        return host.isEmpty() || consumerKey.isEmpty() || consumerSecret.isEmpty();
    }
}
