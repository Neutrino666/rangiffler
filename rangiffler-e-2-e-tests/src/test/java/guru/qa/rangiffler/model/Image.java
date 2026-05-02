package guru.qa.rangiffler.model;

import guru.qa.rangiffler.jupiter.extension.allure.AllureDockerExtension;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.ToString;

@Getter
@ParametersAreNonnullByDefault
public enum Image {

  CYBER_DUCK("cyberpunk-duck.png"),
  MAN("man.jpeg");

  @ToString.Include
  private final String fileName;
  private final String dirResources;

  Image(String value) {
    final String imgPrefix = AllureDockerExtension.IN_DOCKER
        ? "img/docker/profile/"
        : "img/profile/";
    this.fileName = value;
    this.dirResources = imgPrefix + value;
  }
}
