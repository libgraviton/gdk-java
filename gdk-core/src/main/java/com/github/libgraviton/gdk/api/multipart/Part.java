package com.github.libgraviton.gdk.api.multipart;

/**
 * Represents a single part of a Multipart request.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class Part {

    private String body;
    private String formName;

    public Part(String body) {
        this.body = body;
    }

    public Part(String body, String formName) {
        this(body);
        this.formName = formName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }
}
