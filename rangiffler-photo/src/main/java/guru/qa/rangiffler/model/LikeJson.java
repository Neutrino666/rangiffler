package guru.qa.rangiffler.model;

import guru.qa.rangiffler.data.LikeEntity;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record LikeJson(
    UUID userId
) {
  public static LikeJson fromLikeEntity(LikeEntity likeEntity) {
    return new LikeJson(likeEntity.getUserId());
  }
}
