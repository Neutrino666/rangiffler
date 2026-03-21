package guru.qa.rangiffler.controller;

import graphql.relay.DefaultConnection;
import graphql.relay.DefaultConnectionCursor;
import graphql.relay.DefaultEdge;
import graphql.relay.DefaultPageInfo;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.SelectedField;
import guru.qa.rangiffler.ex.TooManySubQueriesException;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.UserPageRequest;
import guru.qa.rangiffler.grpc.UserPageResponse;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Country;
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
    UserResponse u = grpcUserdataClient.getCurrentUser(principalUsername);
    return gqlUserFromGrpcUser(u);
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
    UserPageResponse users = grpcUserdataClient.listUsers(
        UserPageRequest.newBuilder()
            .setPage(page)
            .setSize(size)
            .setSearchQuery(searchQuery == null ? "" : searchQuery)
            .setUsername(principalUsername)
            .build()
    );
    return gqlUserConnectionFromGrpcUserPage(users);
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

  private DefaultConnection<User> gqlUserConnectionFromGrpcUserPage(UserPageResponse users) {
    List<User> pageUsers = users.getEdgesList()
        .stream()
        .map(this::gqlUserFromGrpcUser)
        .toList();
    List<Edge<User>> edges = IntStream.range(0, pageUsers.size())
        .mapToObj(idx -> new DefaultEdge<>(
            pageUsers.get(idx),
            new DefaultConnectionCursor(String.valueOf(idx))
        ))
        .collect(Collectors.toList());
    PageInfo pageInfo = new DefaultPageInfo(
        edges.isEmpty() ? null : edges.getFirst().getCursor(),
        edges.isEmpty() ? null : edges.getLast().getCursor(),
        !users.getFirst(),
        !users.getLast()
    );
    return new DefaultConnection<>(edges, pageInfo);
  }

  private User gqlUserFromGrpcUser(UserResponse u) {
    CountryResponse country = grpcGeoClient.getCountry(u.getCountry().name().toLowerCase());
    return User.newBuilder()
        .id(u.getId())
        .username(u.getUsername())
        .firstname(u.getFirstname())
        .surname(u.getSurname())
        .avatar(u.getAvatar())
        .location(
            Country.newBuilder()
                .code(country.getCode())
                .name(country.getName())
                .flag(country.getFlag())
                .build()
        )
        .build();
  }
}
