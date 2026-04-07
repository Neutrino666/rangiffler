package guru.qa.rangiffler.model;

import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.CountryStat;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.graphqlTypes.Stat;

@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.NONE)
public final class StatGql {

  public static Stat fromGrpcStat(final CountryStat countryStat, final CountryResponse country) {
    return Stat.newBuilder()
        .count(countryStat.getCount())
        .country(CountryGql.fromGrpcCountry(country))
        .build();
  }
}
