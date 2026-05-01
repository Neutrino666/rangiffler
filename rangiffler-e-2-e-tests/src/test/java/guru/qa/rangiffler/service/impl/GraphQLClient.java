package guru.qa.rangiffler.service.impl;

import com.apollographql.adapter.core.DateAdapter;
import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Query;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.client.ApolloClient;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.rangiffler.config.Config;
import guru.qa.type.Date;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public abstract class GraphQLClient {

  protected final ApolloClient apolloClient;
  protected final Config CFG = Config.getInstance();

  public GraphQLClient() {
    this.apolloClient = new ApolloClient.Builder()
        .serverUrl(CFG.gatewayUrl() + "graphql")
        .addCustomScalarAdapter(Date.type, DateAdapter.INSTANCE)
        .okHttpClient(
            new OkHttpClient.Builder()
                .addInterceptor(new AllureOkHttp3())
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
                .build()
        )
        .build();
  }

  @Nonnull
  public <T extends Query.Data> T response(Query<T> query, String token) {
    final ApolloCall<T> currenciesCall = apolloClient.query(query)
        .addHttpHeader("authorization", "Bearer " + token);
    return Rx2Apollo.single(currenciesCall).blockingGet().dataOrThrow();
  }

  @Nonnull
  public <T extends Mutation.Data> T response(Mutation<T> mutation, String token) {
    final ApolloCall<T> currenciesCall = apolloClient.mutation(mutation)
        .addHttpHeader("authorization", "Bearer " + token);
    return Rx2Apollo.single(currenciesCall).blockingGet().dataOrThrow();
  }
}
