package guru.qa.rangiffler.service.impl;

import guru.qa.CreatePhotoMutation;
import guru.qa.LikePhotoMutation;
import guru.qa.rangiffler.helpers.ImageUtils;
import guru.qa.rangiffler.model.enums.Country;
import guru.qa.rangiffler.model.enums.TravelPhotoImage;
import guru.qa.type.CountryInput;
import guru.qa.type.LikeInput;
import guru.qa.type.PhotoInput;
import io.qameta.allure.Step;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PhotoGraphQlClient extends GraphQLClient {

  private final @Nonnull String token;

  public PhotoGraphQlClient(String token) {
    this.token = token;
  }

  @Step("Создаём: фото")
  public CreatePhotoMutation.Photo createPhoto(TravelPhotoImage img, Country country, String description) {
    return response(
        CreatePhotoMutation.builder()
            .input(
                PhotoInput.builder()
                    .country(CountryInput.builder().code(country.getCode()).build())
                    .src(ImageUtils.base64(img))
                    .description(description)
                    .build()
            ).build(),
        token
    ).photo;
  }

  @Step("Обновляем: лайк")
  public LikePhotoMutation.Photo updateLike(
      UUID ownerId,
      UUID requestedId,
      TravelPhotoImage img,
      Country country,
      String description) {
    return response(
        LikePhotoMutation.builder()
            .input(
                PhotoInput.builder()
                    .id(ownerId.toString())
                    .src(ImageUtils.base64(img))
                    .country(CountryInput.builder().code(country.getCode()).build())
                    .description(description)
                    .like(LikeInput.builder().user(requestedId.toString()).build())
                    .build()
            )
            .build(),
        token
    ).photo;
  }
}
