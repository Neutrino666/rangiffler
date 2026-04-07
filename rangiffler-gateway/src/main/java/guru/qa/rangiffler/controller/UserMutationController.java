package guru.qa.rangiffler.controller;

import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.user.UserGql;
import guru.qa.rangiffler.model.user.UserGrpcRequest;
import guru.qa.rangiffler.service.api.GrpcGeoClient;
import guru.qa.rangiffler.service.api.GrpcUserdataClient;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rangiffler.graphqlTypes.FriendshipInput;
import rangiffler.graphqlTypes.User;
import rangiffler.graphqlTypes.UserInput;

@Controller
@PreAuthorize("isAuthenticated()")
@NoArgsConstructor(access = AccessLevel.NONE)
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
    final UserResponse user = switch (input.getAction()) {
      case ADD -> grpcUserdataClient.sendInvitation(principalUsername, input.getUser());
      case ACCEPT -> grpcUserdataClient.acceptInvitation(principalUsername, input.getUser());
      case REJECT -> grpcUserdataClient.declineInvitation(principalUsername, input.getUser());
      case DELETE -> grpcUserdataClient.removeFriend(principalUsername, input.getUser());
    };
    final CountryResponse country = grpcGeoClient.getCountry(user.getCountry().name().toLowerCase());
    return UserGql.fromGrpcUser(user, country);
  }

  @MutationMapping
  public User user(
      @AuthenticationPrincipal Jwt principal,
      @Valid @Argument UserInput input) {
    final String principalUsername = principal.getClaim("sub");
    final UserResponse user = grpcUserdataClient.updateUser(UserGrpcRequest.fromGqlUserInput(input, principalUsername));
    final CountryResponse country = grpcGeoClient.getCountry(user.getCountry().name().toLowerCase());
    return UserGql.fromGrpcUser(user, country);
  }
}
