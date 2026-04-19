package guru.qa.rangiffler.test.ui;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.page.auth.LoginPage;
import guru.qa.rangiffler.page.auth.WelcomePage;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("Идентификация => Аутентификация => Авторизация")
@ParametersAreNonnullByDefault
public class LoginTest {

  private LoginPage loginPage;

  @BeforeEach
  void before() {
    loginPage = Selenide.open(WelcomePage.URL, WelcomePage.class)
        .goToLoginPage();
  }

  @Test
  @DisplayName("Успешный вход")
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    loginPage.login("admin", "admin")
        .getHeader()
        .checkThatComponentLoaded();
  }

  @Test
  @DisplayName("Ошибка не валидных логина / пароля")
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    loginPage.setUsername("notExistUser")
        .setPassword("wrongPass")
        .submit()
        .checkError("Bad credentials");
  }
}
