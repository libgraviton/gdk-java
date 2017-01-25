package com.github.libgraviton.gdk;

import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.header.HeaderBag;
import com.github.libgraviton.gdk.api.multipart.Part;
import com.github.libgraviton.gdk.data.GravitonBase;
import com.github.libgraviton.gdk.exception.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extra Graviton API functionality for /file endpoint calls.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class GravitonFileEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(GravitonFileEndpoint.class);

    private GravitonApi gravitonApi;

    public GravitonFileEndpoint() {
        this.gravitonApi = new GravitonApi();
    }

    public GravitonFileEndpoint(GravitonApi gravitonApi) {
        this.gravitonApi = gravitonApi;
    }

    public GravitonRequest.Builder getFile(String url) {
        LOG.debug("Requesting file");

        // without the 'Accept' - 'application/json' header, we get the file instead of the metadata
        HeaderBag headers = new HeaderBag.Builder()
                .set("Content-Type", "application/json")
                .build();

        return gravitonApi.request()
                .setUrl(url)
                .setHeaders(headers)
                .get();
    }

    public GravitonRequest.Builder getFile(GravitonBase resource) {
        return getFile(gravitonApi.extractId(resource), resource.getClass());
    }

    public GravitonRequest.Builder getFile(String id, Class clazz) {
        return getFile(gravitonApi.getEndpointManager().getEndpoint(clazz.getName()).getItemUrl()).addParam("id", id);
    }

    public GravitonRequest.Builder getMetadata(String url) {
        LOG.debug("Requesting file metadata");
        return gravitonApi.get(url);
    }

    public GravitonRequest.Builder getMetadata(GravitonBase resource) {
        return getMetadata(gravitonApi.extractId(resource), resource.getClass());
    }

    public GravitonRequest.Builder getMetadata(String id, Class clazz) {
        return getMetadata(gravitonApi.getEndpointManager().getEndpoint(clazz.getName()).getItemUrl()).addParam("id", id);
    }

    public GravitonRequest.Builder post(byte[] data, GravitonBase resource) throws SerializationException {
        Part dataPart = new Part(data, "upload");
        Part metadataPart = new Part(gravitonApi.serializeResource(resource), "metadata");

        return gravitonApi.request()
                .setUrl(gravitonApi.getEndpointManager().getEndpoint(resource.getClass().getName()).getUrl())
                .setHeaders(new HeaderBag.Builder().build())
                .post(dataPart, metadataPart);
    }

    public GravitonRequest.Builder put(byte[] data, GravitonBase resource) throws SerializationException {
        Part dataPart = new Part(data, "upload");
        Part metadataPart = new Part(gravitonApi.serializeResource(resource), "metadata");

        return gravitonApi.request()
                .setUrl(gravitonApi.getEndpointManager().getEndpoint(resource.getClass().getName()).getUrl())
                .addParam("id", gravitonApi.extractId(resource))
                .setHeaders(new HeaderBag.Builder().build())
                .put(dataPart, metadataPart);
    }

    public GravitonRequest.Builder patch(GravitonBase resource) throws SerializationException {
        return gravitonApi.patch(resource);
    }

    public GravitonRequest.Builder delete(GravitonBase resource) {
        return gravitonApi.delete(resource);
    }

}
