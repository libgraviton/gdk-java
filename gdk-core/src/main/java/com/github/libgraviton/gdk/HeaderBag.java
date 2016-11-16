package com.github.libgraviton.gdk;

import okhttp3.Headers;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class HeaderBag {

    private Headers headers;

    HeaderBag(Headers headers) {
        this.headers = headers;
    }

    public List<String> getHeader(String headerName) {
        return headers.values(headerName);
    }

    public String getHeaderLine(String headerName) {
        return StringUtils.join(headers.values(headerName), "; ");
    }

}
