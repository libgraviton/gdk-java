package com.github.libgraviton.gdk;

import java.io.Serializable;

/**
 * Represents a Graviton endpoint.
 */
public class Endpoint implements Serializable {

    /**
     * The url of the endpoint.
     */
    private String url;

    /**
     * The url of the endpoint schema.
     */
    private String schemaUrl;

    /**
     * Constructor. Sets the endpoint url.
     *
     * @param url The endpoint url.
     */
    public Endpoint(String url) {
        this.url = url;
    }

    /**
     * Constructor. Sets the item and collection endpoint url.
     *
     * @param url The item endpoint url.
     * @param schemaUrl The collection endpoint url.
     */
    public Endpoint(String url, String schemaUrl) {
        this.url = url;
        this.schemaUrl = schemaUrl;
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof Endpoint)) {
            return false;
        }
        Endpoint endpoint = (Endpoint) obj;
        return ((null == url && null == endpoint.url) || (null != url && url.equals(endpoint.url))) &&
                ((null == schemaUrl && null == endpoint.schemaUrl) ||
                    (null != schemaUrl && schemaUrl.equals(endpoint.schemaUrl)));
    }
}
