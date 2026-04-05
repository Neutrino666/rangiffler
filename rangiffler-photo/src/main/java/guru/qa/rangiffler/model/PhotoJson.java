package guru.qa.rangiffler.model;

import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.util.ByteAsString;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record PhotoJson(
    UUID id,
    UUID userId,
    String country,
    String description,
    String photo,
    Date createdDate,
    List<LikeJson> likes
) {

  public static PhotoJson fromEntity(final PhotoEntity pe) {
    return new PhotoJson(
        pe.getId(),
        pe.getUserId(),
        pe.getCountry(),
        pe.getDescription(),
        new ByteAsString(pe.getPhoto()).string(),
        pe.getCreatedDate(),
        pe.getLikes()
            .stream()
            .map(LikeJson::fromLikeEntity)
            .toList()
    );
  }
}
