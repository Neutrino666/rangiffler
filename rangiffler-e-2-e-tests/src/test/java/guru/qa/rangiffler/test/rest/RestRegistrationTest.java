package guru.qa.rangiffler.test.rest;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.service.impl.AuthApiClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("smoke")
@Severity(BLOCKER)
@Feature("Авторизация")
@DisplayName(TestPrefix.REST + "Registration")
public final class RestRegistrationTest extends BaseRestTest {

  private final AuthApiClient authApiClient = new AuthApiClient();

  @Test
  @DisplayName(TestPrefix.REST + "Регистрация нового пользователя")
  void newUserShouldRegisteredByApiCall1() {
    authApiClient.register(RandomDataUtils.getRandomUserName(), RandomDataUtils.getRandomPassword(), HttpStatus.SC_CREATED);
  }

  @ValueSource(strings = {"sd", "12345567890123"})
  @ParameterizedTest(name = TestPrefix.REST_NEGATIVE + "Регистрация нового пользователя с паролем {0}")
  void newUserShouldNotRegisteredWhenPasswordLessThen3(String password) {
    authApiClient.register(RandomDataUtils.getRandomUserName(), password, HttpStatus.SC_CLIENT_ERROR);
  }

  @ValueSource(strings = {"sd", "12345567890123455678901234556789012345567890123455678901"})
  @ParameterizedTest(name = TestPrefix.REST_NEGATIVE + "Не успешная регистрация нового пользователя")
  void newUserShouldNotRegisteredWhenLoginLessThen3(String login) {
    authApiClient.register(login, RandomDataUtils.getRandomPassword(), HttpStatus.SC_CLIENT_ERROR);
  }
}
