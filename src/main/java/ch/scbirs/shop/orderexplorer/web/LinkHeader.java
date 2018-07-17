package ch.scbirs.shop.orderexplorer.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LinkHeader {

    private static final Pattern LINK = Pattern.compile("<?([^>]*)>(.*)");
    private static final Pattern PARTS = Pattern.compile("\\s*(.+)\\s*=\\s*\"?([^\"]+)\"?");


    private final Map<String, URL> links = new HashMap<>();


    public LinkHeader(List<String> headers) {
        for (String header : headers) {
            parse(header);
        }
    }

    private void parse(String header) {
        Arrays.stream(header.split(",\\s*<"))
                .map(this::parseLink)
                .filter(Objects::nonNull)
                .filter(this::hasRel)
                .forEach(link -> {
                    links.put(link.parts.get("rel"), link.url);
                });

    }

    private boolean hasRel(Link l) {
        return l.parts.containsKey("rel");
    }

    private Link parseLink(String v) {
        Matcher matcher = LINK.matcher(v);
        if (!matcher.matches()) {
            return null;
        }
        String linkUrl = matcher.group(1);
        String[] parts = matcher.group(2).split(";");
        URL parsedUrl = null;
        try {
            parsedUrl = new URL(linkUrl);
        } catch (MalformedURLException e) {
            return null;
        }

        Map<String, String> partsHash = Arrays.stream(parts)
                .skip(1)
                .map(PARTS::matcher)
                .filter(Matcher::matches)
                .collect(Collectors.toMap(m -> m.group(1), m -> m.group(2)));


        return new Link(parsedUrl, partsHash);
    }

    public Optional<URL> get(String key) {
        return Optional.ofNullable(links.get(key));
    }

    private static class Link {
        private final URL url;
        private final Map<String, String> parts;

        private Link(URL url, Map<String, String> parts) {

            this.url = url;
            this.parts = parts;
        }

        @Override
        public String toString() {
            return url + " " + parts;
        }
    }

}
