package guru.qa.rangiffler.service.impl;

import static org.apache.commons.lang.ArrayUtils.isNotEmpty;
import static org.assertj.core.api.Assertions.assertThat;

import guru.qa.rangiffler.api.rest.core.ThreadSafeCookieStore;
import guru.qa.rangiffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public abstract class RestClient {

  protected static final Config CFG = Config.getInstance();

  private final OkHttpClient client;
  private final Retrofit retrofit;

  public RestClient(String baseUrl, boolean followRedirect, @Nullable Interceptor... interceptors) {
    this(baseUrl,
        followRedirect,
        JacksonConverterFactory.create(),
        HttpLoggingInterceptor.Level.HEADERS,
        ThreadSafeCookieStore.INSTANCE,
        interceptors);
  }

  public RestClient(
      String baseUrl,
      boolean followRedirect,
      @Nullable CookieStore store,
      @Nullable Interceptor... interceptors) {
    this(baseUrl,
        followRedirect,
        JacksonConverterFactory.create(),
        HttpLoggingInterceptor.Level.HEADERS,
        store,
        interceptors);
  }

  public RestClient(String baseUrl, HttpLoggingInterceptor.Level level) {
    this(baseUrl, false, JacksonConverterFactory.create(), level, ThreadSafeCookieStore.INSTANCE);
  }

  public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory,
      HttpLoggingInterceptor.Level level, CookieStore store, @Nullable Interceptor... interceptors) {
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
        .followRedirects(followRedirect);

    if (isNotEmpty(interceptors)) {
      for (Interceptor interceptor : interceptors) {
        clientBuilder.addNetworkInterceptor(interceptor);
      }
    }

    clientBuilder
        .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level))
        .addNetworkInterceptor(
            new AllureOkHttp3()
                .setRequestTemplate("http-request.ftl")
                .setResponseTemplate("http-response.ftl")
        )
        .cookieJar(
            new JavaNetCookieJar(
                new CookieManager(
                    store,
                    CookiePolicy.ACCEPT_ALL
                )
            )
        );

    this.client = clientBuilder.build();
    this.retrofit = new Retrofit.Builder()
        .client(this.client)
        .baseUrl(baseUrl)
        .addConverterFactory(converterFactory)
        .build();
  }

  @Nonnull
  public <T> T create(final Class<T> service) {
    return this.retrofit.create(service);
  }

  @Nonnull
  protected <T> Response<T> execute(Supplier<Call<T>> retrofitCall) {
    return execute(retrofitCall, HttpStatus.SC_OK);
  }

  @Nonnull
  protected <T> Response<T> execute(Supplier<Call<T>> retrofitCall, int statusCode) {
    try {
      final Response<T> response = retrofitCall.get().execute();
      assertThat(response.code()).isEqualTo(statusCode);
      return response;
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }
}
