package com.github.libgraviton.gdk;

import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.header.HeaderBag;
import com.github.libgraviton.gdk.api.multipart.Part;
import com.github.libgraviton.gdk.data.GravitonBase;
import com.github.libgraviton.gdk.exception.NoCorrespondingEndpointException;
import com.github.libgraviton.gdk.exception.SerializationException;

/**
 * Extra Graviton API functionality for /file endpoint calls.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class GravitonFile {

    private Graviton graviton;

    public GravitonFile(Graviton graviton) {
        this.graviton = graviton;
    }

    public GravitonRequest.ExecutableBuilder getFile(String url) {
        // without the 'Accept' - 'application/json' header, we get the file instead of the metadata
        HeaderBag headers = new HeaderBag.Builder()
                .set("Content-Type", "application/json")
                .build();

        return (GravitonRequest.ExecutableBuilder) graviton.request()
                .setUrl(url)
                .setHeaders(headers)
                .get();
    }

    public GravitonRequest.ExecutableBuilder getMetadata(String url) {
        return graviton.get(url);
    }

    public GravitonRequest.ExecutableBuilder post(String data, GravitonBase resource) throws NoCorrespondingEndpointException, SerializationException {
        Part dataPart = new Part(data, "upload");
        Part metadataPart = new Part(graviton.serializeResource(resource));

        return (GravitonRequest.ExecutableBuilder) graviton.request()
                .setUrl(graviton.getEndpointManager().getEndpoint(resource.getClass().getName()).getUrl())
                .post(dataPart, metadataPart);
    }

    public GravitonRequest.ExecutableBuilder put(String data, GravitonBase resource) throws NoCorrespondingEndpointException, SerializationException {
        Part dataPart = new Part(data, "upload");
        Part metadataPart = new Part(graviton.serializeResource(resource));

        return (GravitonRequest.ExecutableBuilder) graviton.request()
                .setUrl(graviton.getEndpointManager().getEndpoint(resource.getClass().getName()).getUrl())
                .addParam("id", graviton.extractId(resource))
                .put(dataPart, metadataPart);
    }

    public GravitonRequest.ExecutableBuilder patch(GravitonBase resource) throws NoCorrespondingEndpointException, SerializationException {
        return graviton.patch(resource);
    }

    public GravitonRequest.ExecutableBuilder delete(GravitonBase resource) throws NoCorrespondingEndpointException {
        return graviton.delete(resource);
    }

}
