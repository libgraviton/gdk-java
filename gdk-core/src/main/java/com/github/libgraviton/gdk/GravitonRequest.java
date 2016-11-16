package com.github.libgraviton.gdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.SerializationException;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.NotImplementedException;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GravitonRequest {

    private Request okhttpRequest;

    private GravitonRequest(Request okhttpRequest) {
        this.okhttpRequest = okhttpRequest;
    }

    Request getOkhttpRequest() {
        return okhttpRequest;
    }

    public String getMethod() {
        return okhttpRequest.method();
    }

    public URL getUrl() {
        return okhttpRequest.url().url();
    }

    public static class Builder {

        private Map<String, String> params = new HashMap<>();

        private String url = "";

        private Request.Builder okhttpBuilder = new Request.Builder();

        private Graviton graviton;

        Builder(Graviton graviton) {
            this.graviton = graviton;
        }

        public Builder param(String paramName, String paramValue) {
            params.put(paramName, paramValue);
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder head() {
            okhttpBuilder.head();
            return this;
        }

        public Builder get() {
            okhttpBuilder.get();
            return this;
        }

        public Builder delete() {
            okhttpBuilder.delete();
            return this;
        }

        public Builder post(Object data) throws SerializationException {
            okhttpBuilder.post(buildBody(data));
            return this;
        }

        public Builder put(Object data) throws SerializationException {
            okhttpBuilder.put(buildBody(data));
            return this;
        }

        public Builder patch(Object data) {
            // @todo
            throw new NotImplementedException("PATCH is not supported so far.");
        }

        public GravitonRequest build() {
            if (params.size() > 0) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    url = url.replaceAll("\\{" + param.getKey() + "\\}", param.getValue());
                }
            }
            okhttpBuilder.url(url);
            return new GravitonRequest(okhttpBuilder.build());
        }

        private RequestBody buildBody(Object data) throws SerializationException {
            String json;
            try {
                json = graviton.getObjectMapper().writeValueAsString(data);
            } catch (JsonProcessingException e) {
                throw new SerializationException(
                        String.format("Cannot serialize '%s' to json.", data.getClass().getName()),
                        e
                );
            }

            return RequestBody.create(Graviton.CONTENT_TYPE, json);
        }

        public GravitonResponse execute() throws CommunicationException {
            return graviton.execute(build());
        }



    }

}
