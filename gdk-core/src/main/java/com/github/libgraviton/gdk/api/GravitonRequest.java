package com.github.libgraviton.gdk.api;

import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.api.header.HeaderBag;
import com.github.libgraviton.gdk.api.multipart.Part;
import com.github.libgraviton.gdk.exception.CommunicationException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GravitonRequest {

    private final URL url;

    private final HttpMethod method;

    private final HeaderBag headers;

    private final String body;

    private final List<Part> parts;

    protected GravitonRequest(Builder builder) throws MalformedURLException{
        method = builder.method;
        url = builder.buildUrl();
        headers = builder.headerBuilder.build();
        body = builder.body;
        parts = builder.parts;
    }

    public URL getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HeaderBag getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public List<Part> getParts() {
        return parts;
    }

    public boolean isMultipartRequest() {
        return parts.size() > 0;
    }

    public static class Builder {

        private String url;

        private Map<String, String> params = new HashMap<>();

        private HttpMethod method = HttpMethod.GET;

        private HeaderBag.Builder headerBuilder = new HeaderBag.Builder();

        private String body;

        private List<Part> parts = new ArrayList<>();

        public Builder(){
            setHeaders(getDefaultHeaders());
        }

        public Builder setUrl(URL url) {
            return setUrl(url.toExternalForm());
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder addParam(String paramName, String paramValue) {
            params.put(paramName, paramValue);
            return this;
        }

        public Builder setParams(Map<String, String> params) {
            this.params = new HashMap<>(params);
            return this;
        }

        public Builder setMethod(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder addHeader(String headerName, String headerValue) {
            return addHeader(headerName, headerValue, false);
        }

        public Builder addHeader(String headerName, String headerValue, boolean override) {
            headerBuilder.set(headerName, headerValue, override);
            return this;
        }

        public Builder setHeaders(HeaderBag headerBag) {
            headerBuilder = new HeaderBag.Builder(headerBag);
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setParts(List<Part> parts) {
            this.parts = parts;
            return this;
        }

        public Builder addPart(Part part) {
            this.parts.add(part);
            return this;
        }

        public Builder head() {
            return setMethod(HttpMethod.HEAD);
        }

        public Builder options() {
            return setMethod(HttpMethod.OPTIONS);
        }

        public Builder get() {
            return setMethod(HttpMethod.GET);
        }

        public Builder delete() {
            return setMethod(HttpMethod.DELETE);
        }

        public Builder post(String data) {
            return setMethod(HttpMethod.POST).setBody(data);
        }

        // Multipart POST request
        public Builder post(Part... parts) {
            for (Part part : parts) {
                addPart(part);
            }
            return setMethod(HttpMethod.POST);
        }

        public Builder put(String data) {
            return setMethod(HttpMethod.PUT).setBody(data);
        }

        // Multipart PUT request
        public Builder put(Part... parts) {
            for (Part part : parts) {
                addPart(part);
            }
            return setMethod(HttpMethod.PUT);
        }

        public Builder patch(String data) {
            return setMethod(HttpMethod.PATCH).setBody(data);
        }

        public GravitonRequest build() throws MalformedURLException {
            return new GravitonRequest(this);
        }

        // TODO make it configurable
        protected HeaderBag getDefaultHeaders() {
            return new HeaderBag.Builder()
                    .set("Content-Type", "application/json")
                    .set("Accept", "application/json")
                    .build();
        }

        protected URL buildUrl() throws MalformedURLException {
            String url = this.url;
            for (Map.Entry<String, String> param : params.entrySet()) {
                url = url.replace(String.format("{%s}", param.getKey()), param.getValue());
            }
            return new URL(url);
        }

        protected Map<String, String> getParams() {
            return params;
        }
    }

    public static class ExecutableBuilder extends Builder {

        private Graviton graviton;

        public ExecutableBuilder(Graviton graviton) {
            super();
            this.graviton = graviton;
        }


        public GravitonResponse execute() throws CommunicationException, MalformedURLException {
            return graviton.execute(build());
        }

    }

}
