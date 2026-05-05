package guru.qa.rangiffler.test.rest;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.assertj.core.api.Assertions.assertThat;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.TestPrefix;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("smoke")
@Severity(BLOCKER)
@Feature("Авторизация")
@DisplayName(TestPrefix.REST + TestIcon.LOGIN + "Login")
public final class RestLoginTest extends BaseRestTest {

  @Test
  @User
  @ApiLogin
  @DisplayName(TestPrefix.REST + "Валидный токен")
  void shouldBeValidToken(@Token String token) {
    assertThat(token).hasSizeGreaterThanOrEqualTo(730)
        .matches("^Bearer [\\w_.-]{90,}$");
  }
}
