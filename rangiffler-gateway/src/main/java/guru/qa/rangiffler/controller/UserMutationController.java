package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.ex.IllegalGqlFieldAccessException;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserRequest.Builder;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.UserGrpcRequest;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
import guru.qa.rangiffler.model.UserGql;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlClient.UserGraphQLMutation;
import rangiffler.graphqlTypes.Country;
import rangiffler.graphqlTypes.CountryInput;
import rangiffler.graphqlTypes.FriendshipInput;
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
  public User friendship(
      @AuthenticationPrincipal Jwt principal,
      @Valid @Argument FriendshipInput input) {
    final String principalUsername = principal.getClaim("sub");

    UserResponse user = switch (input.getAction()) {
      case ADD -> grpcUserdataClient.sendInvitation(principalUsername, input.getUser());
      case ACCEPT -> grpcUserdataClient.acceptInvitation(principalUsername, input.getUser());
      case REJECT -> grpcUserdataClient.declineInvitation(principalUsername, input.getUser());
      case DELETE -> grpcUserdataClient.removeFriend(principalUsername, input.getUser());
    };
    CountryResponse country = grpcGeoClient.getCountry(user.getCountry().name().toLowerCase());
    return UserGql.fromGrpcUser(user, country);
  }

  @MutationMapping
  public User user(
      @AuthenticationPrincipal Jwt principal,
      @Valid @Argument UserInput input) {
    final String principalUsername = principal.getClaim("sub");
    UserResponse user = grpcUserdataClient.updateUser(UserGrpcRequest.fromGqlUserInput(input, principalUsername));
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
}
