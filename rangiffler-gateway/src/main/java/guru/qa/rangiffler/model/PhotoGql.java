package guru.qa.rangiffler.model;

import guru.qa.rangiffler.grpc.CountryPageResponse;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.PhotoResponse;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.graphqlTypes.Country;
import rangiffler.graphqlTypes.Likes;
import rangiffler.graphqlTypes.Photo;

@NoArgsConstructor(access = AccessLevel.NONE)
@ParametersAreNonnullByDefault
public final class PhotoGql {

  public static Photo fromGrpsPhoto(
      final PhotoResponse photo,
      final CountryPageResponse allCountries) {
    CountryResponse country = allCountries
        .getAllCountriesList()
        .stream()
        .filter(countryRes -> countryRes.getCode().equals(photo.getCountry().toLowerCase()))
        .findFirst()
        .orElseThrow();
    return Photo.newBuilder()
        .id(photo.getId())
        .src(photo.getSrc())
        .country(
            Country.newBuilder()
                .code(country.getCode())
                .name(country.getName())
                .flag(country.getFlag())
                .build()
        )
        .description(photo.getDescription())
        .isOwner(true)
        .likes(
            Likes.newBuilder()
                .total(0)
                .likes(List.of())
                .build()
        )
        .build();
  }
}
