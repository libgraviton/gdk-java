package com.github.libgraviton.gdk;

import java.io.Serializable;

/**
 * Represents a Graviton service.
 */
public class Service implements Serializable {

    /**
     * The url of the item endpoint.
     */
    private String itemUrl;

    /**
     * The url of the collection endpoint.
     */
    private String collectionUrl;

    /**
     * Constructor. Sets the item endpoint url - for item services only.
     *
     * @param itemUrl The item endpoint url.
     */
    public Service(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    /**
     * Constructor. Sets the item and collection endpoint url.
     *
     * @param itemUrl The item endpoint url.
     * @param collectionUrl The collection endpoint url.
     */
    public Service(String itemUrl, String collectionUrl) {
        this.itemUrl = itemUrl;
        this.collectionUrl = collectionUrl;
    }

    /**
     * Gets the collection endpoint url.
     *
     * @return The collection endpoint url.
     */
    public String getCollectionUrl() {
        return collectionUrl;
    }

    /**
     * Gets the item endpoint url.
     *
     * @return The item endpoint url.
     */
    public String getItemUrl() {
        return itemUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof Service)) {
            return false;
        }
        Service service = (Service) obj;
        return ((null == itemUrl && null == service.itemUrl) || (null != itemUrl && itemUrl.equals(service.itemUrl))) &&
                ((null == collectionUrl && null == service.collectionUrl) ||
                    (null != collectionUrl && collectionUrl.equals(service.collectionUrl)));
    }
}
