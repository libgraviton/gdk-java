package com.github.libgraviton.gdk.api;

import com.github.libgraviton.gdk.RequestExecutor;
import com.github.libgraviton.gdk.api.header.HeaderBag;
import com.github.libgraviton.gdk.api.multipart.Part;
import com.github.libgraviton.gdk.api.query.Query;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.UnsuccessfulRequestException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private URL url;

    private HttpMethod method;

    private HeaderBag headers;

    private byte[] body;

    private List<Part> parts;

    protected Request() {
    }

    protected Request(Builder builder) throws MalformedURLException {
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

    public byte[] getBodyBytes() {
        return body;
    }

    public String getBody() {
        return body != null ? new String(body) : null;
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

        private Query query;

        private byte[] body;

        private List<Part> parts = new ArrayList<>();

        private RequestExecutor executor;

        public Builder() {
            this.executor = new RequestExecutor();
        }

        public Builder(RequestExecutor executor) {
            this.executor = executor;
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

        public Builder setQuery(Query query) {
            if(this.query == null) {
                this.query = query;
            } else {
                this.query.addStatements(query.getStatements());
            }

            return this;
        }

        public Builder setBody(String body) {
            this.body = body.getBytes();
            return this;
        }

        public Builder setBody(byte[] body) {
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

        public Request build() throws MalformedURLException {
            return new Request(this);
        }

        public Response execute() throws CommunicationException {
            try {
                return executor.execute(build());
            } catch (MalformedURLException e) {
                throw new UnsuccessfulRequestException(String.format("'%s' to '%s' failed due to malformed url.", method, url),
                        e);
            }
        }

        protected URL buildUrl() throws MalformedURLException {
            String generatedQuery = query != null ? query.generate() : "";
            String url = this.url + generatedQuery;
            for (Map.Entry<String, String> param : params.entrySet()) {
                url = url.replace(String.format("{%s}", param.getKey()), param.getValue());
            }

            return new URL(url);
        }

        protected Map<String, String> getParams() {
            return params;
        }
    }

}
