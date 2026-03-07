package guru.qa.rangiffler.controller;

import jakarta.validation.Valid;
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

  @MutationMapping
  public User user(
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
