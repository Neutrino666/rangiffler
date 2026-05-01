package guru.qa.rangiffler.model;

import guru.qa.type.FriendStatus;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record FriendshipJson(
    UUID id,
    String username,
    FriendStatus status
) {

}
