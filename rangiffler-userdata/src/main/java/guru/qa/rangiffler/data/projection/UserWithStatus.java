package guru.qa.rangiffler.data.projection;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.FriendshipStatus;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.UUID;

public record UserWithStatus(
    @Nonnull UUID id,
    @Nonnull String username,
    String firstname,
    String surname,
    CountryValues country,
    byte[] avatar,
    FriendshipStatus status,
    @Nullable UUID requesterId,
    @Nullable UUID addresseeId
) {
}
