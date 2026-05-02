package guru.qa.rangiffler.page.auth;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.page.BasePage;
import guru.qa.rangiffler.page.TravelsPage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ParametersAreNonnullByDefault
public final class LoginPage extends BasePage<LoginPage> {

  public static final @Nonnull String URL = CFG.authUrl() + "login";

  private final SelenideElement usernameInput = $("input[ name = 'username' ]").as("Поле ввода: логин");
  private final SelenideElement passwordInput = $("input[ name = 'password' ]").as("Поле ввода: пароль");
  private final SelenideElement submitBtn = $("button[ type = 'submit' ]").as("Кнопка: авторизоваться");
  private final SelenideElement registerLink = $(".form__link").as("Ссылка на страницу регистрации");
  private final SelenideElement formError = $(".form__error").as("Ошибка в форме авторизации");

  @Step("Авторизация username: '{username}' password: '{password}'")
  public @Nonnull TravelsPage login(final String username, final String password) {
    setUsername(username);
    setPassword(password);
    submit();
    return new TravelsPage();
  }

  @Step("Открытие страницы регистрации")
  public @Nonnull RegistrationPage openRegistrationPage() {
    registerLink.click();
    return new RegistrationPage();
  }

  @Step("Ввод username: '{username}'")
  public @Nonnull LoginPage setUsername(final String username) {
    usernameInput.val(username);
    return this;
  }

  @Step("Ввод password: '{password}'")
  public @Nonnull LoginPage setPassword(final String password) {
    passwordInput.val(password);
    return this;
  }

  @Step("Клик submit")
  public @Nonnull LoginPage submit() {
    submitBtn.click();
    return this;
  }

  @Step("Проверка текста ошибки: '{message}'")
  public @Nonnull LoginPage checkError(final String message) {
    formError.shouldHave(text(message), visible);
    return this;
  }

  @Step("Проверяем что страница прогрузилась")
  public @Nonnull LoginPage checkThatPageLoaded() {
    SelenideUtils.visibleAndInteractable(usernameInput, passwordInput, submitBtn, registerLink);
    return this;
  }
}
