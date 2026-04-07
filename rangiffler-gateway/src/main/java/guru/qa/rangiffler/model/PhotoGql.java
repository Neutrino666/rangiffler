package guru.qa.rangiffler.model;

import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.PhotoResponse;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.graphqlTypes.Like;
import rangiffler.graphqlTypes.Likes;
import rangiffler.graphqlTypes.Photo;

@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public final class PhotoGql {

  @Nonnull
  public static Photo fromGrpsPhoto(
      final PhotoResponse photo,
      final CountryPageResponse allCountries) {
    final CountryResponse country = allCountries
        .getAllCountriesList()
        .stream()
        .filter(countryRes ->
            countryRes.getCode()
                .equals(photo.getCountry().name().toLowerCase())
        )
        .findFirst()
        .orElseThrow();
    final Likes likesGql = Likes.newBuilder()
        .total(photo.getLikeCount())
        .likes(photo.getLikeList().stream()
            .map(l -> Like.newBuilder()
                .user(l.getUserId())
                .build())
            .toList())
        .build();
    return Photo.newBuilder()
        .id(photo.getId())
        .src(photo.getSrc())
        .country(CountryGql.fromGrpcCountry(country))
        .description(photo.getDescription())
        .isOwner(photo.getIsOwner())
        .likes(likesGql)
        .build();
  }
}
