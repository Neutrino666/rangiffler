package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
import org.springframework.beans.factory.annotation.Autowired;
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
