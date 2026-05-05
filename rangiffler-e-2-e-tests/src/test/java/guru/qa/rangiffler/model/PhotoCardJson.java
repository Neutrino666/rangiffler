package guru.qa.rangiffler.model;

import guru.qa.rangiffler.helpers.ImageUtils;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.TravelPhotoImage;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record PhotoCardJson(
    TravelPhotoImage img,
    int likes,
    Country country,
    String description
) {

  @Nonnull
  public List<String> toExpectedCondition() {
    return List.of(
        ImageUtils.base64(img),
        likes + " likes",
        country.getName(),
        description
    );
  }

  public static PhotoCardJson fromPhotoAnno(Photo photo) {
    return new PhotoCardJson(
        photo.img(),
        photo.likes(),
        photo.country(),
        photo.description()
    );
  }
}
