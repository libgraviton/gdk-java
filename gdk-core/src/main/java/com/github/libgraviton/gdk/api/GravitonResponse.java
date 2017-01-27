package com.github.libgraviton.gdk.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.libgraviton.gdk.api.header.HeaderBag;
import com.github.libgraviton.gdk.exception.DeserializationException;
import com.github.libgraviton.gdk.serialization.JsonPatcher;

import java.io.IOException;
import java.util.List;

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

    private byte[] body;

    private boolean isSuccessful;

    private ObjectMapper objectMapper;

    protected GravitonResponse() {
    }

    protected GravitonResponse(GravitonResponse.Builder builder) {
        request = builder.request;
        code = builder.code;
        isSuccessful = builder.isSuccessful;
        message = builder.message;
        body = builder.body;
        headers = builder.headerBuilder.build();
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    /**
     * Deserialize the response body into the requested POJO class.
     *
     * @param beanClass the requested POJO class
     * @param <BeanClass> requested POJO must extend from this class
     * @return serialized POJO
     * @throws DeserializationException will be thrown on a failed deserialization / POJO mapping
     */
    public <BeanClass> BeanClass getBodyItem(final Class<? extends BeanClass> beanClass) throws DeserializationException {
        if(objectMapper == null) {
            throw new IllegalStateException("'objectMapper' is not allowed to be null.");
        }

        try {
            BeanClass pojoValue = objectMapper.readValue(getBody(), beanClass);
            JsonPatcher.add(pojoValue, objectMapper.valueToTree(pojoValue));
            return pojoValue;
        } catch (IOException e) {
            throw new DeserializationException(String.format(
                    "Unable to deserialize response body from '%s' to class '%s'.",
                    request.getUrl(),
                    beanClass.getName()
            ), e);
        }
    }

    /**
     * Deserialize the response body into a list with elements of the requested POJO class.
     *
     * @param beanClass the requested POJO class
     * @param <BeanClass> requested POJO must extend from this class
     * @return serialized list with POJO elements
     * @throws DeserializationException will be thrown on a failed deserialization / POJO mapping
     */
    public <BeanClass> List<BeanClass> getBodyItems(final Class<? extends BeanClass> beanClass) throws DeserializationException {
        if(objectMapper == null) {
            throw new IllegalStateException("'objectMapper' is not allowed to be null.");
        }

        try {
            final CollectionType javaType =
                    objectMapper.getTypeFactory().constructCollectionType(List.class, beanClass);
            List<BeanClass> pojoValues = objectMapper.readValue(getBody(), javaType);
            for (BeanClass pojoValue : pojoValues) {
                JsonPatcher.add(pojoValue, objectMapper.valueToTree(pojoValue));
            }
            return pojoValues;
        } catch (IOException e) {
            throw new DeserializationException(String.format(
                    "Unable to deserialize response body from '%s' to class '%s'.",
                    request.getUrl(),
                    beanClass.getName()
            ), e);
        }
    }

    /**
     * Deserialize the response body. Should only be used if no binary file is expected as response.
     * @return response body as String
     */
    public String getBody() {
        return new String(body);
    }

    /**
     * Deserialize the response body. Can also be used if binary file is expected as response.
     *
     * @return response body
     */
    public byte[] getBodyBytes() {
        return body;
    }

    public HeaderBag getHeaders() {
        return headers;
    }

    public GravitonRequest getRequest() {
        return request;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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

        private byte[] body;

        private boolean isSuccessful;

        private HeaderBag.Builder headerBuilder;

        public Builder(GravitonRequest request) {
            this.request = request;
            headerBuilder = new HeaderBag.Builder();
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder successful(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder body(byte[] body) {
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
