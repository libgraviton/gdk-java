package com.github.libgraviton.gdk.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.api.header.HeaderBag;
import com.github.libgraviton.gdk.exception.SerializationException;
import java.io.IOException;

/**
 * Graviton response wrapper with additional functionality and simplified interface.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class GravitonResponse {

    private GravitonRequest request;

    private int code;

    private  String message;

    private HeaderBag headers;

    private String body;

    private ObjectMapper objectMapper;

    GravitonResponse(GravitonResponse.Builder builder) {
        request = builder.request;
        code = builder.code;
        message = builder.message;
        body = builder.body;
        headers = builder.headerBuilder.build();
        objectMapper = builder.objectMapper;
    }

    public boolean isSuccessful() {
        return false;
    }

    public <BeanClass> BeanClass getBody(Class<? extends BeanClass> beanClass) throws SerializationException {
        try {
            return objectMapper.readValue(getBody(), beanClass);
        } catch (IOException e) {
            throw new SerializationException(String.format(
                    "Unable to deserialize response setBody from '%s' to class '%s'.",
                    request.getUrl(),
                    beanClass.getName()
            ), e);
        }
    }

    public String getBody() {
        return body;
    }

    public HeaderBag getHeaders() {
        return headers;
    }

    public GravitonRequest getRequest() {
        return request;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static class Builder {

        private int code;

        private String message;

        private GravitonRequest request;

        private String body;

        private ObjectMapper objectMapper;

        private HeaderBag.Builder headerBuilder;

        public Builder(GravitonRequest request) {
            this(request, new ObjectMapper());
        }

        public Builder(GravitonRequest request, ObjectMapper objectMapper) {
            this.request = request;
            this.objectMapper = objectMapper;
            headerBuilder = new HeaderBag.Builder();
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder headers(HeaderBag.Builder builder) {
            this.headerBuilder = builder;
            return this;
        }

        public GravitonResponse build() {
            return new GravitonResponse(this);
        }

    }

}
