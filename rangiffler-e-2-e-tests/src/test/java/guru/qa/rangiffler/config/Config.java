package guru.qa.rangiffler.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface Config {

  @Nonnull
  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.INSTANCE
        : LocalConfig.INSTANCE;
  }

  String projectId = "rangiffler-amurskiy-a";

  @Nonnull
  String frontUrl();

  @Nonnull
  String authUrl();

  @Nonnull
  String gatewayUrl();

  @Nonnull
  String userdataUrl();

  @Nonnull
  String photoUrl();

  @Nonnull
  String geoUrl();

  @Nonnull
  String screenshotBaseDir();

  @Nonnull
  String allureDockerUrl();
}
