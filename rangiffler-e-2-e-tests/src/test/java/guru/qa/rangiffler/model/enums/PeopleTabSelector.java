package guru.qa.rangiffler.model.enums;

import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@Getter
@ParametersAreNonnullByDefault
public enum PeopleTabSelector {

  FRIENDS("friends"),
  ALL("all"),
  OUTCOME("outcome"),
  INCOME("income");

  private final String value;

  PeopleTabSelector(String value) {
    this.value = "#simple-tabpanel-" + value;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "." + name();
  }
}
