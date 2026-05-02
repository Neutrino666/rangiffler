package guru.qa.rangiffler.model;

import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.ToString;

@Getter
@ParametersAreNonnullByDefault
public enum PhotoFile {

  CYBER_DUCK("cyberpunk-duck.png"),
  MAN("man.jpeg");

  @ToString.Include
  private final String fileName;
  private final String dirResources;

  PhotoFile(String value) {
    this.fileName = value;
    this.dirResources = "img/" + value;
  }
}
