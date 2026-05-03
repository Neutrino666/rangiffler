package guru.qa.rangiffler.page.auth;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.page.BasePage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ParametersAreNonnullByDefault
public final class RegistrationPage extends BasePage<LoginPage> {

  public static final @Nonnull String URL = CFG.authUrl() + "register";

  private final SelenideElement self = $(".content__wrapper").as("Страница регистрации");
  private final SelenideElement logo = self.$(".header__logo").as("Логотип");

  private final SelenideElement siteTitle = self.$(".header").as("Название сайта");
  private final SelenideElement usernameInput = self.$("#username").as("Поле ввода: username");
  private final SelenideElement passwordInput = self.$("#password").as("Поле ввода: password");
  private final SelenideElement passwordSubmitInput = self.$("#passwordSubmit").as("Поле ввода: passwordSubmit");
  private final SelenideElement submitRegistrationBtn = self.$("button.form__submit")
      .as("Кнопка подтверждения ввода данных регистрации");
  private final SelenideElement signInBtn = $(".form__link");
  private final SelenideElement congratulationSignInBtn = $(".form_sign-in");
  private final SelenideElement formError = $(".form__error");
  private final SelenideElement switchShowPassword = self.$("#passwordBtn")
      .as("Кнопка сокрытия пароля: \uD83D\uDC41\uFE0F");


  @Step(TestIcon.REGISTRATION + "Открываем. Страницу регистрации")
  public static RegistrationPage open() {
    return Selenide.open(URL, RegistrationPage.class);
  }

  @Step(TestIcon.REGISTRATION + "Проверяем что страница прогрузилась")
  public @Nonnull RegistrationPage checkThatPageLoaded() {
    SelenideUtils.visible(self, logo, siteTitle);
    SelenideUtils.visibleAndInteractable(
        usernameInput, passwordInput, passwordSubmitInput, submitRegistrationBtn, switchShowPassword
    );
    return this;
  }

  @Step(TestIcon.REGISTRATION + "Регистрация пользователя")
  public @Nonnull WelcomePage registrationUser(final String username,
      final String password) {
    setUsername(username);
    setPassword(password);
    setPasswordSubmit(password);
    submitRegistration();
    congratulationSignIn();
    return new WelcomePage();
  }

  @Step(TestIcon.REGISTRATION + "Ввод username: '{username}'")
  public @Nonnull RegistrationPage setUsername(final String username) {
    usernameInput.val(username);
    return this;
  }

  @Step(TestIcon.REGISTRATION + "Ввод password: '{password}'")
  public @Nonnull RegistrationPage setPassword(final String password) {
    passwordInput.val(password);
    return this;
  }

  @Step(TestIcon.REGISTRATION + "Ввод подтверждения password: '{password}'")
  public @Nonnull RegistrationPage setPasswordSubmit(final String password) {
    passwordSubmitInput.val(password);
    return this;
  }

  @Step(TestIcon.REGISTRATION + "Клик подтверждения регистрации")
  public @Nonnull RegistrationPage submitRegistration() {
    submitRegistrationBtn.click();
    return this;
  }

  @Step(TestIcon.REGISTRATION + "Клик signIn")
  public @Nonnull RegistrationPage signIn() {
    signInBtn.click();
    return this;
  }

  @Step(TestIcon.REGISTRATION + "Клик signIn")
  public @Nonnull LoginPage congratulationSignIn() {
    congratulationSignInBtn.click();
    return new LoginPage();
  }

  @Step(TestIcon.REGISTRATION + "Проверка текста ошибки: '{message}'")
  public @Nonnull RegistrationPage checkError(final String message) {
    formError.shouldHave(text(message));
    return this;
  }
}
