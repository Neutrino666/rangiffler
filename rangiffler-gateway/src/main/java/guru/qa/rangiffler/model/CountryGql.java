package guru.qa.rangiffler.model;

import guru.qa.rangiffler.grpc.CountryResponse;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.graphqlTypes.Country;

@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public final class CountryGql {

  public static Country fromGrpcCountry(final CountryResponse country) {
    return Country.newBuilder()
        .code(country.getCode())
        .name(country.getName())
        .flag(country.getFlag())
        .build();
  }
}
