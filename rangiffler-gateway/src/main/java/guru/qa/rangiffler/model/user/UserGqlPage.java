package guru.qa.rangiffler.model.user;

import graphql.relay.DefaultConnection;
import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.UserPageResponse;
import guru.qa.rangiffler.model.PageGql;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import rangiffler.graphqlTypes.User;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.NONE)
@ParametersAreNonnullByDefault
public final class UserGqlPage {

  public static DefaultConnection<User> fromGrpcUserPage(
      final UserPageResponse users,
      final CountryPageResponse countries) {
    final List<User> pageUsers = users.getEdgesList()
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
    return new PageGql<>(pageUsers, users.getFirst(), users.getLast()).
        connection();
  }
}
