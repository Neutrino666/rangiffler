package guru.qa.rangiffler.model;

import guru.qa.CreatePhotoMutation;
import guru.qa.rangiffler.helpers.ImageUtils;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record PhotoJson(
    @Nullable UUID id,
    String src,
    CountryJson country,
    int likes,
    String description
) {

  public PhotoJson setLikes(int likes) {
    return new PhotoJson(
        id,
        src,
        country,
        likes,
        description
    );
  }

  public static PhotoJson fromPhotoCard(PhotoCardJson photo) {
    return new PhotoJson(
        null,
        ImageUtils.base64(photo.img()),
        new CountryJson(
            photo.country().getCode(),
            photo.country().getName(),
            null
        ),
        photo.likes(),
        photo.description()
    );
  }

  public static PhotoJson fromGraphQlCreatedPhoto(CreatePhotoMutation.Photo photo) {
    return new PhotoJson(
        UUID.fromString(photo.id),
        photo.src,
        new CountryJson(
            photo.country.code,
            photo.country.name,
            photo.country.flag
        ),
        photo.likes.total,
        photo.description
    );
  }

  public static PhotoJson fromPhotoAnno(Photo photo) {
    return new PhotoJson(
        null,
        ImageUtils.base64(photo.img()),
        new CountryJson(
            photo.country().getCode(),
            photo.country().getName(),
            null
        ),
        0,
        photo.description()
    );
  }

  @Nonnull
  public List<String> toExpectedCondition() {
    return List.of(
        src,
        likes + " likes",
        country.name(),
        description
    );
  }
}
