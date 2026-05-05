package guru.qa.rangiffler.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
enum DockerConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://frontend.rangiffler.dc/";
  }

  @Nonnull
  public String authUrl() {
    return "http://auth.rangiffler.dc:9000/";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://gateway.rangiffler.dc:8080/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://userdata.rangiffler.dc:8089/";
  }

  @Nonnull
  @Override
  public String photoUrl() {
    return "http://photo.rangiffler.dc:8093/";
  }

  @NotNull
  @Override
  public String geoUrl() {
    return "http://geo.rangiffler.dc:8091/";
  }

  @NotNull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/selenoid/";
  }

  @Nonnull
  @Override
  public String allureDockerUrl() {
    final String allureDockerApiFromEnv = System.getenv("ALLURE_DOCKER_API");
    return allureDockerApiFromEnv != null
        ? allureDockerApiFromEnv
        : "http://allure:5050/";
  }
}
