package com.github.libgraviton.gdk.api.gateway;

import com.github.libgraviton.gdk.Graviton;
import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
import com.github.libgraviton.gdk.api.header.Header;
import com.github.libgraviton.gdk.api.header.HeaderBag;
import com.github.libgraviton.gdk.exception.CommunicationException;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OkHttpGateway implements GravitonGateway {

    private OkHttpClient okHttp;

    public OkHttpGateway() {
        this(new OkHttpClient());
    }

    public OkHttpGateway(OkHttpClient okHttp) {
        this.okHttp = okHttp;
    }

    public GravitonResponse execute(GravitonRequest request) throws CommunicationException {
        Request.Builder requestBuilder = new Request.Builder();
        RequestBody okHttpBody = null == request.getBody() ?
                null : RequestBody.create(Graviton.CONTENT_TYPE, request.getBody());
        Request okHttpRequest = requestBuilder
                .method(request.getMethod().asString(), okHttpBody)
                .url(request.getUrl())
                .headers(createHeaders(request.getHeaders()))
                .build();

        Response okHttpResponse;
        String body;
        try {
            okHttpResponse = okHttp.newCall(okHttpRequest).execute();
            body = okHttpResponse.body().string();
        } catch (IOException e) {
            throw new CommunicationException(
                    String.format("'%s' to '%s' failed.", request.getMethod(), request.getUrl()),
                    e
            );
        }

        GravitonResponse.Builder responseBuilder = new GravitonResponse.Builder(request);
        return responseBuilder
                .code(okHttpResponse.code())
                .headers(createHeaders(okHttpResponse.headers()))
                .message(okHttpResponse.message())
                .successful(okHttpResponse.isSuccessful())
                .body(body)
                .build();
    }

    private Headers createHeaders(HeaderBag headerBag) {
        Headers.Builder builder = new Headers.Builder();
        for (Map.Entry<String, Header> header : headerBag.all().entrySet()) {
            for (String value : header.getValue()) {
                builder.add(header.getKey(), value);
            }
        }
        return builder.build();
    }

    private HeaderBag.Builder createHeaders(Headers okhttpHeaders) {
        HeaderBag.Builder builder = new HeaderBag.Builder();
        for (Map.Entry<String, List<String>> header : okhttpHeaders.toMultimap().entrySet()) {
            builder.set(header.getKey(), header.getValue());
        }
        return builder;
    }

}
