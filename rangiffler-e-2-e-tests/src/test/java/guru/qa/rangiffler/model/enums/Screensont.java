package guru.qa.rangiffler.model.enums;

import guru.qa.rangiffler.config.Config;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@Getter
@ParametersAreNonnullByDefault
public enum Screensont {

  PROFILE_CYBER_DUCK("cyberpunk-duck.png"),
  PROFILE_MAN("man.jpeg"),
  TRAVEL_MAP_EMPTY("empty_map.png"),
  TRAVEL_MAP_KZ2_RU1("kz2_ru1_map.png"),
  TRAVEL_MAP_KZ1("kz1_map.png"),;

  private final String fileName;
  private final String dirResources;
  private final String screenOutput;

  Screensont(String value) {
    this.fileName = value;
    this.dirResources = Config.getInstance().screenshotBaseDir() + this.fileName;
    this.screenOutput = ".screen-output/" + this.dirResources;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "." + name();
  }
}
