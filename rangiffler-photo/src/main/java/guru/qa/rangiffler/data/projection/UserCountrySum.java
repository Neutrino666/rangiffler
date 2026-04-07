package guru.qa.rangiffler.data.projection;

import guru.qa.rangiffler.data.CountryValues;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record UserCountrySum(
    long count,
    CountryValues country
) {

}
