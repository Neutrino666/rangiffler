package guru.qa.rangiffler.page.auth;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.BasePage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ParametersAreNonnullByDefault
public final class WelcomePage extends BasePage<LoginPage> {

  public static final String URL = CFG.authUrl();

  private final SelenideElement loginBtn = $(".MuiButton-containedPrimary").as("Кнопка: логин");
  private final SelenideElement registerBtn = $(".MuiButton-outlinedPrimary").as(
      "Кнопка-ссылка на страницу регистрации");

  @Step("Проверяем что страница прогрузилась")
  public @Nonnull WelcomePage checkThatPageLoaded() {
    loginBtn.should(visible);
    registerBtn.should(visible);
    return this;
  }

  @Step("Открываем страницу регистрации")
  public @Nonnull LoginPage goToLoginPage() {
    loginBtn.click();
    return new LoginPage();
  }
}
