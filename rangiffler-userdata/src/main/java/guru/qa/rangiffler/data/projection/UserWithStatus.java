package guru.qa.rangiffler.data.projection;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.FriendshipStatus;
import java.util.UUID;

public record UserWithStatus(
    UUID id,
    String username,
    String firstname,
    String surname,
    CountryValues country,
    FriendshipStatus status,
    byte[] avatar
) {
}
