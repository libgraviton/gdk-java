package com.github.libgraviton.gdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.SerializationException;
import okhttp3.Headers;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Graviton response wrapper with additional functionality and simplified interface.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class GravitonResponse {

    private Response response;

    private HeaderBag headerBag;

    private String responseBody;

    private ObjectMapper objectMapper = new ObjectMapper();

    GravitonResponse(Response response, ObjectMapper objectMapper) throws CommunicationException {
        this.response = response;
        this.objectMapper = objectMapper;
        headerBag = new HeaderBag(response.headers());
        try {
            responseBody = response.body().string();
        } catch (IOException e) {
            throw new CommunicationException(String.format(
                    "Unable to fetch response body from '%s' to '%s'.",
                    response.request().method(),
                    response.request().url()
            ), e);
        } finally {
            response.close();
        }
    }

    public <BeanClass> BeanClass getBody(Class<? extends BeanClass> beanClass) throws SerializationException {
        try {
            return objectMapper.readValue(getBody(), beanClass);
        } catch (IOException e) {
            throw new SerializationException(String.format(
                    "Unable to deserialize response body from '%s' to class '%s'.",
                    response.request().url(),
                    beanClass.getName()
            ), e);
        }
    }

    public String getBody() {
        return responseBody;
    }

    /**
     * Returns the link that has rel="eventStatus". For example, the 'Link' header might look like the following
     *
     * Link: <http://localhost:8000/some/graviton/endpoint/1234>; rel="self",<http://localhost:8000/event/status/20c3b1f9c3b83d339bd88e8e5b0d7066>; rel="eventStatus"
     *
     * @return link that has rel="eventStatus"
     */
    public String getEventStatusLink() {
        return getLink("eventStatus");
    }

    public String getLink(String rel) {
        Headers headers = response.headers();

        if (null == headers) {
            return null;
        }

        List<String> links = headers.values("Link");
        if (links != null) {
            String linkHeaderSelfPattern = "(?<=<)((?!<).)*(?=>; *rel=\"" + rel + "\")";
            for (String link : links) {
                Matcher matcher = Pattern.compile(linkHeaderSelfPattern).matcher(link);
                if (matcher.find()) {
                    return matcher.group(0);
                }
            }
        }
        return null;
    }

    public String getLocation() {
        return response.header("Location");
    }

    protected void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HeaderBag getHeaders() {
        return headerBag;
    }
}
