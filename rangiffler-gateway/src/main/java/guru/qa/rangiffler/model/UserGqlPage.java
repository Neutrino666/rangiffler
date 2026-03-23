package guru.qa.rangiffler.model;

import graphql.relay.DefaultConnection;
import graphql.relay.DefaultConnectionCursor;
import graphql.relay.DefaultEdge;
import graphql.relay.DefaultPageInfo;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.UserPageResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import rangiffler.graphqlTypes.User;

@NoArgsConstructor(access = AccessLevel.NONE)
@RequiredArgsConstructor
public class UserGqlPage {

  public static DefaultConnection<User> fromGrpcUserPage(
      final UserPageResponse users,
      final CountryPageResponse countries) {
    List<User> pageUsers = users.getEdgesList()
        .stream()
        .map(u -> {
          CountryResponse country = countries.getAllCountriesList()
              .stream()
              .filter(cc -> cc.getCode().equals(u.getCountry().name().toLowerCase()))
              .findFirst()
              .orElseThrow();
          return UserGql.fromGrpcUser(u, country);
        })
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
}
