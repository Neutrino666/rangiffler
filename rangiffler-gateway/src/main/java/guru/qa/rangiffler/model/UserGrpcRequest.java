package guru.qa.rangiffler.model;

import guru.qa.rangiffler.grpc.UserRequest;
import guru.qa.rangiffler.grpc.UserRequest.Builder;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.graphqlTypes.UserInput;

@NoArgsConstructor(access = AccessLevel.NONE)
@ParametersAreNonnullByDefault
public final class UserGrpcRequest {

  public static UserRequest fromGqlUserInput(final UserInput userInput, final String username) {
    Builder userBuilder = UserRequest.newBuilder();
    userBuilder.setUsername(username);
    if (userInput.hasAvatar()) {
      userBuilder.setAvatar(userInput.getAvatar());
    }
    if (userInput.hasFirstname()) {
      userBuilder.setFirstname(userInput.getFirstname());
    }
    if (userInput.hasSurname()) {
      userBuilder.setSurname(userInput.getSurname());
    }
    if (userInput.hasLocation()) {
      userBuilder.setCountry(userInput.getLocation().getCode());
    }
    if (userInput.hasAvatar()) {
      userBuilder.setAvatar(userInput.getAvatar());
    }
    return userBuilder.build();
  }
}
