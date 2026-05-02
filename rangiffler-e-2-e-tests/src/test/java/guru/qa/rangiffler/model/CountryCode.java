package guru.qa.rangiffler.model;

import guru.qa.rangiffler.grpc.CountryValues;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@Getter
@ParametersAreNonnullByDefault
public enum CountryCode {

  RU(CountryValues.RU),
  US(CountryValues.US),
  KZ(CountryValues.KZ);

  private final CountryValues countryValue;
  private final String value;

  CountryCode(CountryValues value) {
    this.countryValue = value;
    this.value = value.name().toLowerCase();
  }
}
