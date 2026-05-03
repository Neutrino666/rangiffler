package guru.qa.rangiffler.model.enums;

import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public enum Country {

  RU("ru", "Russian Federation"),
  US("us", "United States"),
  KZ("kz", "Kazakhstan");

  private final String code;
  private final String name;

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "." + name();
  }
}
