package guru.qa.rangiffler.controller;

import graphql.relay.DefaultConnection;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import guru.qa.rangiffler.ex.TooManySubQueriesException;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.UserPageRequest;
import guru.qa.rangiffler.grpc.UserPageResponse;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.UserGqlPage;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
import guru.qa.rangiffler.model.UserGql;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.User;

@Controller
@PreAuthorize("isAuthenticated()")
public class UserQueryController {

  private final GrpcUserdataClient grpcUserdataClient;
  private final GrpcGeoClient grpcGeoClient;

  @Autowired
  public UserQueryController(GrpcUserdataClient grpcUserdataClient, GrpcGeoClient grpcGeoClient) {
    this.grpcUserdataClient = grpcUserdataClient;
    this.grpcGeoClient = grpcGeoClient;
  }

  @QueryMapping
  public User user(@AuthenticationPrincipal Jwt principal) {
    final String principalUsername = principal.getClaim("sub");
    final UserResponse u = grpcUserdataClient.getCurrentUser(principalUsername);
    final CountryResponse country = grpcGeoClient.getCountry(u.getCountry().name().toLowerCase());
    return UserGql.fromGrpcUser(u, country);
  }

  @QueryMapping
  public DefaultConnection<User> users(
      @AuthenticationPrincipal Jwt principal,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery,
      @Nonnull DataFetchingEnvironment env) {
    checkSubQueries(env, "friends");
    final String principalUsername = principal.getClaim("sub");
    final UserPageResponse users = grpcUserdataClient.listUsers(
        UserPageRequest.newBuilder()
            .setPage(page)
            .setSize(size)
            .setSearchQuery(searchQuery == null ? "" : searchQuery)
            .setUsername(principalUsername)
            .build()
    );
    return UserGqlPage.fromGrpcUserPage(users, grpcGeoClient.getCountries());
  }

  @SchemaMapping(typeName = "User", field = "outcomeInvitations")
  public DefaultConnection<User> outcomeInvitations(
      @AuthenticationPrincipal Jwt principal,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery,
      @Nonnull DataFetchingEnvironment env
  ) {
    checkSubQueries(env, "friends");
    final String principalUsername = principal.getClaim("sub");
    final UserPageResponse users = grpcUserdataClient.listOutcomeInvitations(
        UserPageRequest.newBuilder()
            .setPage(page)
            .setSize(size)
            .setSearchQuery(searchQuery == null ? "" : searchQuery)
            .setUsername(principalUsername)
            .build()
    );
    return UserGqlPage.fromGrpcUserPage(users, grpcGeoClient.getCountries());
  }

  @SchemaMapping(typeName = "User", field = "incomeInvitations")
  public DefaultConnection<User> incomeInvitations(
      @AuthenticationPrincipal Jwt principal,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery,
      @Nonnull DataFetchingEnvironment env
  ) {
    checkSubQueries(env, "friends");
    final String principalUsername = principal.getClaim("sub");
    final UserPageResponse users = grpcUserdataClient.listIncomeInvitations(
        UserPageRequest.newBuilder()
            .setPage(page)
            .setSize(size)
            .setSearchQuery(searchQuery == null ? "" : searchQuery)
            .setUsername(principalUsername)
            .build()
    );
    return UserGqlPage.fromGrpcUserPage(users, grpcGeoClient.getCountries());
  }

  @SchemaMapping(typeName = "User", field = "friends")
  public DefaultConnection<User> friends(
      @AuthenticationPrincipal Jwt principal,
      @Argument int page,
      @Argument int size,
      @Argument @Nullable String searchQuery,
      @Nonnull DataFetchingEnvironment env
  ) {
    checkSubQueries(env, "friends");
    final String principalUsername = principal.getClaim("sub");
    UserPageResponse users = grpcUserdataClient.listFriends(
        UserPageRequest.newBuilder()
            .setPage(page)
            .setSize(size)
            .setSearchQuery(searchQuery == null ? "" : searchQuery)
            .setUsername(principalUsername)
            .build()
    );
    return UserGqlPage.fromGrpcUserPage(users, grpcGeoClient.getCountries());
  }

  private void checkSubQueries(@Nonnull DataFetchingEnvironment env, @Nonnull String... queryKeys) {
    for (String queryKey : queryKeys) {
      List<SelectedField> selectors = env.getSelectionSet().getFieldsGroupedByResultKey()
          .get(queryKey);
      if (selectors != null && selectors.size() > 1) {
        throw new TooManySubQueriesException("Can`t fetch over 1 " + queryKey + " sub-queries");
      }
    }
  }
}
