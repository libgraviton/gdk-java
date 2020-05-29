package com.github.libgraviton.gdk.api.gateway.okhttp;

import com.github.libgraviton.gdk.util.okhttp.interceptor.RetryInterceptor;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

public class OkHttpGatewayFactory {

  // create a trust manager that does not validate certificate chains
  final private static TrustManager[] trustAllCerts = new TrustManager[]{
      new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
          String i;
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
          String i;
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }
      }
  };

  /**
   * returns the normal okhttpclient that can have our retry interceptor
   *
   * @return instance
   */
  public static OkHttpClient getInstance(Boolean hasRetry) {
    return getBaseBuilder(true).build();
  }

  /**
   * gets the normal instance without retry interceptor
   *
   * @return instance
   */
  public static OkHttpClient getInstance() {
    return getBaseBuilder(false).build();
  }

  /**
   * returns a client that trusts all certs
   *
   * @param hasRetry if should have retry or not
   * @return
   * @throws Exception
   */
  public static OkHttpClient getAllTrustingInstance(Boolean hasRetry, OkHttpClient baseClient) throws Exception {
    SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

    Builder baseBuilder;
    if (baseClient != null) {
      baseBuilder = baseClient.newBuilder();
    } else {
      baseBuilder = getBaseBuilder(hasRetry);
    }

    baseBuilder
        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
        .hostnameVerifier((hostname, session) -> true);

    return baseBuilder.build();
  }

  private static Builder getBaseBuilder(Boolean hasRetry) {
    Builder builder = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS);

    if (hasRetry) {
      builder.addInterceptor(new RetryInterceptor());
    }

    return builder;
  }
}
