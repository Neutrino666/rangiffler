package guru.qa.rangiffler.model;

import guru.qa.rangiffler.data.CountryValues;
import guru.qa.rangiffler.data.projection.UserCountrySum;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record StatisticJson(
    int count,
    CountryValues country
) {

  public static StatisticJson fromUserCountrySum(final UserCountrySum userCountrySum) {
    return new StatisticJson(
        (int) userCountrySum.count(),
        userCountrySum.country()
    );
  }

  public static List<StatisticJson> fromUserCountrySums(final List<UserCountrySum> userCountrySums) {
    return userCountrySums.stream()
        .map(StatisticJson::fromUserCountrySum)
        .toList();
  }
}