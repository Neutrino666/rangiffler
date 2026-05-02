package guru.qa.rangiffler.test.ui.auth;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.auth.LoginPage;
import guru.qa.rangiffler.page.auth.WelcomePage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Severity(BLOCKER)
@Feature("Авторизация")
@DisplayName(TestPrefix.UI + "Идентификация => Аутентификация => Авторизация")
@ParametersAreNonnullByDefault
public class LoginTest {

  private LoginPage loginPage;

  @BeforeEach
  void before() {
    loginPage = WelcomePage.open().goToLoginPage();
  }

  @User
  @Test
  @DisplayName(TestPrefix.UI_POSITIVE + "Успешный вход")
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
    String username = user.getUsername();
    String password = user.getTestData().password();
    loginPage.login(username, password)
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName(TestPrefix.UI_NEGATIVE + "Ошибка не валидных логина / пароля")
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    loginPage.setUsername("notExistUser")
        .setPassword("wrongPass")
        .submit()
        .checkError("Bad credentials");
  }
}
