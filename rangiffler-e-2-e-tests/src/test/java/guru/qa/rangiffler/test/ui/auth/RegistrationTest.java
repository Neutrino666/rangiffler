package guru.qa.rangiffler.test.ui.auth;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.helpers.RandomDataUtils;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.auth.RegistrationPage;
import guru.qa.rangiffler.page.auth.WelcomePage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@WebTest
@Tag("smoke")
@Severity(BLOCKER)
@Feature("Авторизация")
@DisplayName(TestPrefix.UI + "Регистрация")
@ParametersAreNonnullByDefault
public class RegistrationTest {

  private RegistrationPage registrationPage;

  @BeforeEach
  void before() {
    registrationPage = WelcomePage.open().goToRegistrationPage();
  }

  @Test
  @DisplayName(TestPrefix.UI_POSITIVE + "Отображение страницы")
  void shouldIsPresentRegisterPage() {
    registrationPage.checkThatPageLoaded();
  }

  @Test
  @DisplayName(TestPrefix.UI_POSITIVE + "Регистрация нового пользователя")
  void shouldRegisterNewUser() {
    String username = RandomDataUtils.getRandomUserName();
    final String password = RandomDataUtils.getRandomPassword();
    registrationPage.registrationUser(username, password)
        .goToLoginPage()
        .login(username, password)
        .checkThatPageLoaded();
  }

  @User
  @Test
  @DisplayName(TestPrefix.UI_NEGATIVE + "Регистрация существующего пользователя")
  void shouldNotRegisterUserWithExistingUsername(final UserJson user) {
    String username = user.getUsername();
    String password = user.getTestData().password();

    registrationPage.setUsername(username)
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkError("Username `%s` already exists".formatted(username));
  }

  @Test
  @DisplayName(TestPrefix.UI_NEGATIVE + "Ошибка не совпадения пароля")
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    registrationPage.setUsername(RandomDataUtils.getRandomUserName())
        .setPassword("wrongPwd")
        .setPasswordSubmit("otherPwd")
        .submitRegistration()
        .checkError("Passwords should be equal");
  }

  @ValueSource(strings = {"te", "aaaaaaaasasadadaasdasasdasssssssssssssssssssssssss1"})
  @ParameterizedTest(name = TestPrefix.UI_NEGATIVE + "Ошибка логина выход за передел диапазона 3 - 50 символов")
  void shouldShowErrorLoginOutOfRange3To50(String username) {
    final String password = RandomDataUtils.getRandomPassword();
    registrationPage.setUsername(username)
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkError("Allowed username length should be from 3 to 50 characters");
  }

  @ValueSource(strings = {"12", "1234567890123"})
  @ParameterizedTest(name = TestPrefix.UI_NEGATIVE + "Ошибка логина выход за передел диапазона 3 - 50 символов")
  void shouldShowErrorPasswordOutOfRange3To50(String password) {
    registrationPage.setUsername(RandomDataUtils.getRandomUserName())
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkError("Allowed password length should be from 3 to 12 characters");
  }

  @Test
  @DisplayName(TestPrefix.UI_NEGATIVE + "Ошибка логин с пробелом")
  void shouldShowErrorIfLoginHasWhitespace() {
    final String password = RandomDataUtils.getRandomPassword();
    registrationPage.setUsername("t test")
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkError("Username must not contain whitespaces");
  }
}
