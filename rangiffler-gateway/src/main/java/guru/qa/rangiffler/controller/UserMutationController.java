package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserRequest.Builder;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.Country;
import rangiffler.graphqlTypes.CountryInput;
import rangiffler.graphqlTypes.User;
import rangiffler.graphqlTypes.UserInput;

@Controller
@PreAuthorize("isAuthenticated()")
public class UserMutationController {

  private final GrpcUserdataClient grpcUserdataClient;
  private final GrpcGeoClient grpcGeoClient;

  @Autowired
  public UserMutationController(GrpcUserdataClient grpcUserdataClient, GrpcGeoClient grpcGeoClient) {
    this.grpcUserdataClient = grpcUserdataClient;
    this.grpcGeoClient = grpcGeoClient;
  }

  @MutationMapping
  public User user(
      @AuthenticationPrincipal Jwt principal,
      @Valid @Argument UserInput input) {
    UserResponse user = grpcUserdataClient.updateUser(fromGql(input, principal));
    CountryResponse country = grpcGeoClient.getCountry(user.getCountry().name().toLowerCase());
    return User.newBuilder()
        .id(user.getId())
        .username(user.getUsername())
        .avatar(input.getAvatar())
        .firstname(input.getFirstname())
        .surname(input.getSurname())
        .location(
            Country.newBuilder()
                .code(country.getCode())
                .name(country.getName())
                .build()
        )
        .build();
  }

  @MutationMapping
  public User users(
      @AuthenticationPrincipal Jwt principal,
      @Valid @Argument UserInput input) {
    final String principalUsername = principal.getClaim("sub");
    CountryInput ci = input.getLocation();
    return User.newBuilder()
        .id("1ba62f37-a3da-4e4f-ac99-40d83daa099c")
        .username("Цап царап не трогай котика) " + principalUsername)
        .avatar(input.getAvatar())
        .firstname(input.getFirstname())
        .surname(input.getSurname())
        .location(
            Country.newBuilder()
                .code(ci.getCode())
                .name("Russia")
                .build()
        )
        .build();
  }

  private UserRequest fromGql(UserInput ui, Jwt principal) {
    final String principalUsername = principal.getClaim("sub");
    Builder userBuilder = UserRequest.newBuilder();
    userBuilder.setUsername(principalUsername);
    if (ui.hasAvatar()) {
      userBuilder.setAvatar(ui.getAvatar());
    }
    if (ui.hasFirstname()) {
      userBuilder.setFirstname(ui.getFirstname());
    }
    if (ui.hasSurname()) {
      userBuilder.setSurname(ui.getSurname());
    }
    if (ui.hasLocation()) {
      userBuilder.setCountry(ui.getLocation().getCode());
    }
    if (ui.hasAvatar()) {
      userBuilder.setAvatar(ui.getAvatar());
    }
    return userBuilder.build();
  }
}
