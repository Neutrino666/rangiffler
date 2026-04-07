package guru.qa.rangiffler.model.user;

import static guru.qa.rangiffler.grpc.FriendshipStatus.NOT_FRIEND;
import static guru.qa.rangiffler.grpc.FriendshipStatus.UNRECOGNIZED;

import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.UserResponse;
import guru.qa.rangiffler.model.CountryGql;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.graphqlTypes.FriendStatus;
import rangiffler.graphqlTypes.User;

@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public final class UserGql {

  public static User fromGrpcUser(final UserResponse user, final CountryResponse country) {
    return User.newBuilder()
        .id(user.getId())
        .username(user.getUsername())
        .firstname(user.getFirstname())
        .surname(user.getSurname())
        .avatar(user.getAvatar())
        .friendStatus(
            user.getFriendshipStatus().equals(UNRECOGNIZED) || user.getFriendshipStatus().equals(NOT_FRIEND)
                ? null
                : FriendStatus.valueOf(user.getFriendshipStatus().name())
        )
        .location(CountryGql.fromGrpcCountry(country))
        .build();
  }
}
