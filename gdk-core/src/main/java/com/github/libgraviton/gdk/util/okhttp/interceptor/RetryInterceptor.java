package com.github.libgraviton.gdk.util.okhttp.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryInterceptor implements Interceptor {

  private static final Logger LOG = LoggerFactory.getLogger(RetryInterceptor.class);

  private ArrayList<Integer> retryHttpCodes = new ArrayList<>();
  private Integer retryCount;
  private Integer waitInBetween;

  private Integer retried = 0;

  public RetryInterceptor() {
    this(
      6 * 10, // 10 minutes
      10
    );
  }

  public RetryInterceptor(Integer retryCount, Integer waitInBetween) {
    this.retryCount = retryCount;
    this.waitInBetween = waitInBetween;

    // we want to retry when these http codes are returned
    retryHttpCodes.add(500);
    retryHttpCodes.add(502);
    retryHttpCodes.add(503);
  }

  @Override public Response intercept(Interceptor.Chain chain) throws IOException {
    // reset counter
    retried = 0;
    Boolean available = false;

    while (!available && (retried < retryCount)) {
      available = isAvailable(chain);
      if (!available) {
        try {
          LOG.info("Waiting for '{}' seconds before next retry...", waitInBetween);
          Thread.sleep(waitInBetween * 1000);
        } catch (InterruptedException retriedException) {
          throw new IOException("System failure, unable to Thread.wait()", retriedException);
        }

        retried++;
      }
    }

    return chain.proceed(chain.request());
  }

  public Boolean isAvailable(Interceptor.Chain chain) {
    // let's build a new request to the root
    HttpUrl thisUrl = chain.request()
        .url()
        .newBuilder()
        .query(null)
        .encodedPath("/")
        .build();

    Request subRequest = chain.request()
        .newBuilder()
        .url(thisUrl)
        .method("OPTIONS", null)
        .build();

    Response response = null;

    try {
      response = chain.proceed(subRequest);

      if (retryHttpCodes.contains(response.code())) {
        LOG.warn(
            "Encountered error status code '{}' when doing test OPTIONS request to URL '{}'",
            response.code(),
            thisUrl
        );
        return false;
      }

      return true;
    } catch (IOException e) {
      LOG.warn(
          "Encountered exception '{}' while trying to connect to URL '{}'",
          e.getClass().getCanonicalName(),
          thisUrl
      );
    } finally {
      if (response != null) {
        response.body().close();
      }
    }

    return false;
  }

  public Integer getRetried() {
    return retried;
  }
}
