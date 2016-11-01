package com.github.libgraviton.gdk;

import java.io.Serializable;

/**
 * Represents a Graviton endpoint.
 */
public class Endpoint implements Serializable {

    /**
     * The url of the endpoint to address a single item.
     */
    private String itemUrl;

    /**
     * The url of the endpoint.
     */
    private String url;

    /**
     * Constructor. Sets the endpoint itemUrl.
     *
     * @param itemUrl The endpoint itemUrl.
     */
    public Endpoint(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    /**
     * Constructor. Sets the item and collection endpoint itemUrl.
     *
     * @param itemUrl The item endpoint itemUrl.
     * @param url The endpoint itemUrl.
     */
    public Endpoint(String itemUrl, String url) {
        this.itemUrl = itemUrl;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof Endpoint)) {
            return false;
        }
        Endpoint endpoint = (Endpoint) obj;
        return ((null == itemUrl && null == endpoint.itemUrl) || (null != itemUrl && itemUrl.equals(endpoint.itemUrl))) &&
                ((null == url && null == endpoint.url) ||
                    (null != url && url.equals(endpoint.url)));
    }
}
