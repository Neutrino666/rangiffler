package guru.qa.rangiffler.model;

import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.PhotoResponse;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.graphqlTypes.Country;
import rangiffler.graphqlTypes.Like;
import rangiffler.graphqlTypes.Likes;
import rangiffler.graphqlTypes.Photo;

@NoArgsConstructor(access = AccessLevel.NONE)
@ParametersAreNonnullByDefault
public final class PhotoGql {

  @Nonnull
  public static Photo fromGrpsPhoto(
      final PhotoResponse photo,
      final CountryPageResponse allCountries) {
    final CountryResponse country = allCountries
        .getAllCountriesList()
        .stream()
        .filter(countryRes -> countryRes.getCode().equals(photo.getCountry().toLowerCase()))
        .findFirst()
        .orElseThrow();
    Likes likesGql = Likes.newBuilder()
        .total(photo.getLikeCount())
        .likes(photo.getLikeList().stream()
            .map(l -> Like.newBuilder()
                .user(l.getUserId())
                .build())
            .toList())
        .build();
    Country countryGql = Country.newBuilder()
        .code(country.getCode())
        .name(country.getName())
        .flag(country.getFlag())
        .build();
    return Photo.newBuilder()
        .id(photo.getId())
        .src(photo.getSrc())
        .country(countryGql)
        .description(photo.getDescription())
        .isOwner(photo.getIsOwner())
        .likes(likesGql)
        .build();
  }
}
