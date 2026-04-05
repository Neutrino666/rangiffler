package guru.qa.rangiffler.controller;

import graphql.relay.DefaultConnection;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import guru.qa.rangiffler.ex.TooManySubQueriesException;
import guru.qa.rangiffler.grpc.FeedRequest;
import guru.qa.rangiffler.grpc.PhotoPageResponse;
import guru.qa.rangiffler.model.PhotoGqlPage;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import guru.qa.rangiffler.service.api.GrpcPhotoClient;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
import jakarta.annotation.Nonnull;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Country;
import rangiffler.graphqlTypes.Feed;
import rangiffler.graphqlTypes.Photo;
import rangiffler.graphqlTypes.Stat;

@Controller
@PreAuthorize("isAuthenticated()")
public class FeedQueryController {

  private final GrpcPhotoClient grpcPhotoClient;
  private final GrpcUserdataClient userdataClient;
  private final GrpcGeoClient grpcGeoClient;

  @Autowired
  public FeedQueryController(final GrpcPhotoClient grpcPhotoClient, final GrpcUserdataClient userdataClient,
      GrpcGeoClient grpcGeoClient) {
    this.grpcPhotoClient = grpcPhotoClient;
    this.userdataClient = userdataClient;
    this.grpcGeoClient = grpcGeoClient;
  }

  @QueryMapping
  public Feed feed(
      final @AuthenticationPrincipal Jwt principal,
      final @Argument Boolean withFriends,
      final @Nonnull DataFetchingEnvironment env
  ) {
    checkSubQueries(env, "photo", "feed");
    final String principalUsername = principal.getClaim("sub");
    return Feed.newBuilder()
        .withFriends(withFriends)
        .username(principalUsername)
        .stat(List.of(
            Stat.newBuilder()
                .count(1)
                .country(Country.newBuilder()
                    .code("ru")
                    .build())
                .build()
        ))
        .build();
  }

  @SchemaMapping(typeName = "Feed", field = "stat")
  public List<Stat> stat(
      final @AuthenticationPrincipal Jwt principal,
      final Feed feed,
      final @Nonnull DataFetchingEnvironment env) {
    checkSubQueries(env, "photo", "feed");
    final String principalUsername = principal.getClaim("sub");
    return List.of(
        Stat.newBuilder()
            .count(1)
            .country(
                Country.newBuilder()
                    .code("ru")
                    .build()
            )
            .build()
    );
  }

  @SchemaMapping(typeName = "Feed", field = "photos")
  public DefaultConnection<Photo> photos(
      final Feed feed,
      final @AuthenticationPrincipal Jwt principal,
      final @Argument int page,
      final @Argument int size,
      final @Nonnull DataFetchingEnvironment env) {
    checkSubQueries(env, "photo", "feed");
    final String principalUsername = principal.getClaim("sub");
    final PhotoPageResponse grpcFeed = grpcPhotoClient.listPhotos(
        FeedRequest.newBuilder()
            .setWithFriends(feed.getWithFriends())
            .setUserId(userdataClient.getCurrentUserId(feed.getUsername()).toString())
            .setSize(size)
            .setPage(page)
            .setUsername(principalUsername)
            .build()
    );
    return PhotoGqlPage.fromGrpcPhotoPage(grpcFeed, grpcGeoClient.getCountries());
  }

  private void checkSubQueries(final @Nonnull DataFetchingEnvironment env, final @Nonnull String... queryKeys) {
    for (String queryKey : queryKeys) {
      final List<SelectedField> selectors = env.getSelectionSet().getFieldsGroupedByResultKey()
          .get(queryKey);
      if (selectors != null && selectors.size() > 1) {
        throw new TooManySubQueriesException("Can`t fetch over 1 " + queryKey + " sub-queries");
      }
    }
  }
}
