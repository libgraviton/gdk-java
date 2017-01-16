package com.github.libgraviton.gdk.api.gateway;

import com.github.libgraviton.gdk.api.GravitonRequest;
import com.github.libgraviton.gdk.api.GravitonResponse;
import com.github.libgraviton.gdk.api.header.Header;
import com.github.libgraviton.gdk.api.header.HeaderBag;
import com.github.libgraviton.gdk.api.multipart.Part;
import com.github.libgraviton.gdk.exception.CommunicationException;
import com.github.libgraviton.gdk.exception.UnsuccessfulRequestException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpGateway implements GravitonGateway {

    private static final Logger LOG = LoggerFactory.getLogger(OkHttpGateway.class);

    private OkHttpClient okHttp;

    public OkHttpGateway() {
        this(
            new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
        );
    }

    public OkHttpGateway(OkHttpClient okHttp) {
        this.okHttp = okHttp;
    }

    public GravitonResponse execute(GravitonRequest request) throws CommunicationException {
        Request okHttpRequest = generateRequest(request);

        Response okHttpResponse;
        byte[] body;
        try {
            okHttpResponse = okHttp.newCall(okHttpRequest).execute();
            body = okHttpResponse.body().bytes();
        } catch (IOException e) {
            throw new UnsuccessfulRequestException(
                    String.format("'%s' to '%s' failed.", request.getMethod(), request.getUrl()),
                    e
            );
        }

        return generateResponse(request, okHttpResponse, body);
    }

    private Request generateRequest(GravitonRequest request) {
        RequestBody okHttpBody;
        if (request.isMultipartRequest()) {
            okHttpBody = generateMultipartRequestBody(request);
        } else {
            okHttpBody = generateDefaultRequestBody(request);
        }

        Request.Builder requestBuilder = new Request.Builder();
        return requestBuilder
                .method(request.getMethod().asString(), okHttpBody)
                .url(request.getUrl())
                .headers(createHeaders(request.getHeaders()))
                .build();
    }

    private RequestBody generateMultipartRequestBody(GravitonRequest request) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (Part part : request.getParts()) {
            MultipartBody.Part bodyPart;
            RequestBody requestBody = RequestBody.create(null, part.getBody());
            if (part.getFormName() != null) {
                bodyPart = MultipartBody.Part.createFormData(part.getFormName(), null, requestBody);
            } else {
                bodyPart = MultipartBody.Part.create(null, requestBody);
            }

            builder.addPart(bodyPart);
        }

        return builder.build();
    }

    private RequestBody generateDefaultRequestBody(GravitonRequest request) {
        return null == request.getBody() ? null :
                RequestBody.create(MediaType.parse(request.getHeaders().get("Content-Type") + "; charset=utf-8"), request.getBody());
    }

    private GravitonResponse generateResponse(GravitonRequest request, Response okHttpResponse, byte[] body) {
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
