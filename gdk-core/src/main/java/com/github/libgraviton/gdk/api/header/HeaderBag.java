package com.github.libgraviton.gdk.api.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderBag {

    private Map<String, Header> headers;

    private HeaderBag(Map<String, Header> headers) {
        this.headers = new HashMap<>(headers);
    }

    public Map<String, Header> all() {
        return new HashMap<>(headers);
    }

    public Header get(String headerName) {
        if (!headers.containsKey(headerName)) {
            headers.put(headerName, new Header());
        }
        return headers.get(headerName);
    }

    public String getLink(LinkHeader linkHeader) {
        return getLink(linkHeader.getRel());
    }

    public String getLink(String rel) {
        if (null == headers) {
            return null;
        }

        Header links = this.get("Link");
        if (links != null) {
            String linkHeaderSelfPattern = "(?<=<)((?!<).)*(?=>; *rel=\"" + rel + "\")";
            for (String link : links) {
                Matcher matcher = Pattern.compile(linkHeaderSelfPattern).matcher(link);
                if (matcher.find()) {
                    return matcher.group(0);
                }
            }
        }
        return null;
    }

    public static class Builder {

        private Map<String, Header> headers;

        public Builder() {
            headers = new HashMap<>();
        }

        public Builder(HeaderBag headerBag) {
            headers = new HashMap<>(headerBag.all());
        }

        public Builder set(String headerName, List<String> headerValues) {
            headers.put(headerName, new Header(headerValues));
            return this;
        }

        public Builder set(String headerName, String headerValue) {
            return set(headerName, headerValue, false);
        }

        public Builder set(String headerName, String headerValue, boolean override) {
            if (!headers.containsKey(headerName) || override) {
                headers.put(headerName, new Header());
            }
            headers.put(headerName, new Header(headers.get(headerName), headerValue));
            return this;
        }

        public HeaderBag build() {
            return new HeaderBag(headers);
        }
    }

}
