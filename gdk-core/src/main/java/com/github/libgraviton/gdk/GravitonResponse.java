package com.github.libgraviton.gdk;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private String responseBody;

    private ObjectMapper objectMapper = new ObjectMapper();

    public GravitonResponse() {
    }

    public GravitonResponse(Response response) {
        this.response = response;
    }

    public Response getOriginalResponse() {
        return response;
    }

    public <BeanClass> BeanClass deserializeBody(Class<? extends BeanClass> beanClass) throws SerializationException {
        try {
            return objectMapper.readValue(getBody(), beanClass);
        } catch (IOException e) {
            throw new SerializationException("Unable to deserialize response body from '" + response.request().url() + "' to class '" + beanClass.getName() + "'.", e);
        }
    }

    public String getBody() {
        return responseBody;
    }

    protected void setBody(String responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * Returns the link that has rel="eventStatus". For example, the 'Link' header might look like the following
     *
     * Link: <http://localhost:8000/some/graviton/endpoint/1234>; rel="self",<http://localhost:8000/event/status/20c3b1f9c3b83d339bd88e8e5b0d7066>; rel="eventStatus"
     *
     * @return link that has rel="eventStatus"
     */
    public String getEventStatusLink() {
        Headers headers = response.headers();

        if (null == headers) {
            return null;
        }

        List<String> links = headers.values("Link");
        if (links != null) {
            String linkHeaderSelfPattern = "(?<=<)((?!<).)*(?=>; *rel=\"eventStatus\")";
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
}
