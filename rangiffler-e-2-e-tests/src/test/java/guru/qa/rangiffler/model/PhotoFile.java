package guru.qa.rangiffler.model;

import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public enum PhotoFile {

  MAN("cyberpunk-duck.png"),
  DOG("man.jpeg");

  private final String value;
}
