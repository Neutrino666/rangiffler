package guru.qa.rangiffler.test.rest;

import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.service.impl.AuthApiClient;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ParametersAreNonnullByDefault
@DisplayName("REST Registration")
public final class RestRegistrationTest extends BaseRestTest {

  private final AuthApiClient authApiClient = new AuthApiClient();
  private final String validPassword = "12345";

  @Test
  @DisplayName("Регистрация нового пользователя")
  void newUserShouldRegisteredByApiCall1() {
    authApiClient.register(RandomDataUtils.getRandomUserName(), validPassword, HttpStatus.SC_CREATED);
  }

  @ValueSource(strings = {"sd", "12345567890123"})
  @ParameterizedTest(name = "[Negative] Регистрация нового пользователя с паролем {0}")
  void newUserShouldNotRegisteredWhenPasswordLessThen3(String password) {
    authApiClient.register(RandomDataUtils.getRandomUserName(), password, HttpStatus.SC_CLIENT_ERROR);
  }

  @ValueSource(strings = {"sd", "12345567890123455678901234556789012345567890123455678901"})
  @ParameterizedTest(name = "[Negative] Регистрация нового пользователя с логином {0}")
  void newUserShouldNotRegisteredWhenLoginLessThen3(String login) {
    authApiClient.register(login, validPassword, HttpStatus.SC_CLIENT_ERROR);
  }
}
