package com.github.libgraviton.gdk.api.multipart;

import java.util.Arrays;

/**
 * Represents a single part of a Multipart request.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class Part {

    private byte[] body;
    private String formName;

    public Part(String body) {
        this(body.getBytes());
    }

    public Part(byte[] body) {
        this.body = body;
    }

    public Part(String body, String formName) {
        this(body);
        this.formName = formName;
    }

    public Part(byte[] body, String formName) {
        this(body);
        this.formName = formName;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    @Override
    public String toString() {
        return "Part{" +
                "formName='" + formName + '\'' +
                ", body='" + (body.length <= 1000 ? new String(body) : new String(Arrays.copyOfRange(body, 0, 1000)) + " ... [body too big to display in its complete beauty]") + '\'' +
                "}";
    }
}
