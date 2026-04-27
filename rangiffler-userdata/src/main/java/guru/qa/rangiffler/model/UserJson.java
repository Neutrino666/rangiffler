package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import guru.qa.rangiffler.util.ByteAsString;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
    @JsonProperty("id")
    @Nullable UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    @Nullable String firstname,
    @JsonProperty("surname")
    @Nullable String surname,
    @JsonProperty("country")
    CountryValues country,
    @JsonProperty("avatar")
    @Nullable String avatar,
    @JsonProperty("friendshipStatus")
    @Nullable FriendshipStatus friendshipStatus) {

  public static @Nonnull UserJson fromEntity(UserEntity entity, @Nullable FriendshipStatus friendshipStatus) {
    return new UserJson(
        entity.getId(),
        entity.getUsername(),
        entity.getFirstname(),
        entity.getSurname(),
        entity.getCountry(),
        entity.getAvatar() != null && entity.getAvatar().length > 0
            ? new ByteAsString(entity.getAvatar()).string()
            : "",
        friendshipStatus
    );
  }

  public static @Nonnull UserJson fromEntity(UserEntity entity) {
    return fromEntity(entity, null);
  }

  public static @Nonnull UserJson fromUserEntityProjection(UserWithStatus projection) {
    FriendshipStatus status = projection.addresseeId() == null
        ? null
        : switch (projection.status()) {
          case PENDING -> projection.addresseeId().equals(projection.id())
                ? FriendshipStatus.INVITATION_SENT
                : FriendshipStatus.INVITATION_RECEIVED;
          case ACCEPTED -> FriendshipStatus.FRIEND;
        };
    return new UserJson(
        projection.id(),
        projection.username(),
        projection.firstname(),
        projection.surname(),
        projection.country(),
        projection.avatar() != null && projection.avatar().length > 0
            ? new ByteAsString(projection.avatar()).string()
            : "",
        status
    );
  }
}
