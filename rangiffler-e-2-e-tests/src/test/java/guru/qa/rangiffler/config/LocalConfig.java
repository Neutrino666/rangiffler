package guru.qa.rangiffler.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
enum LocalConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://localhost:3001/";
  }

  @Nonnull
  public String authUrl() {
    return "http://localhost:9000/";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://localhost:8080/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://localhost:8089/";
  }

  @Nonnull
  @Override
  public String photoUrl() {
    return "http://localhost:8093/";
  }

  @NotNull
  @Override
  public String geoUrl() {
    return "http://localhost:8091/";
  }

  @Nonnull
  @Override
  public String allureDockerUrl() {
    return "http://allure:5050/";
  }
}
