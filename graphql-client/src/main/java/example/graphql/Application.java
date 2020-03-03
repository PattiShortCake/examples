package example.graphql;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.fetcher.ApolloResponseFetchers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

  public static void main(final String[] args) {
    SpringApplication.run(Application.class);

    final ApolloClient apolloClient = ApolloClient.builder()
        .defaultResponseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
        .callFactory(new OAuthCallFactory())
        .build();

  }

  private static OkHttpClient okHttpClient() {
    return new OkHttpClient.Builder()
//        .sslSocketFactory()
        .build();
  }

  public static class OAuthCallFactory implements Call.Factory {

    OkHttpClient delegate = okHttpClient();

    @Override
    public Call newCall(final Request request) {
      // TODO Get Token
      return delegate.newCall(
          new Request.Builder()
              .addHeader("Authorization", "Bearer: token")
              .build()

      );
    }
  }
}
