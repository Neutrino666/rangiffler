package guru.qa.rangiffler.page.auth;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.page.BasePage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@ParametersAreNonnullByDefault
public final class WelcomePage extends BasePage<LoginPage> {

  public static final String URL = CFG.authUrl();

  private final SelenideElement self = $("div .landing__wrapper").as("Приветственная страница");

  private final SelenideElement loginBtn = self.$(".MuiButton-containedPrimary").as("Кнопка: логин");
  private final SelenideElement registerBtn = self.$(".MuiButton-outlinedPrimary").as(
      "Кнопка-ссылка на страницу регистрации");
  private final SelenideElement logo = self.$(".landing__logo").as("Логотип");
  private final SelenideElement siteTitle = self.$(".landing__header").as("Название сайта");

  @Step("Открываем. Приветственную страницу")
  public static WelcomePage open() {
    return Selenide.open(URL, WelcomePage.class);
  }

  @Step("Проверяем что страница прогрузилась")
  public @Nonnull WelcomePage checkThatPageLoaded() {
    SelenideUtils.visible(self, logo, siteTitle);
    SelenideUtils.visibleAndInteractable(loginBtn, registerBtn);
    return this;
  }

  @Step("Открываем страницу входа")
  public @Nonnull LoginPage goToLoginPage() {
    loginBtn.click();
    return new LoginPage();
  }

  @Step("Открываем страницу регистрации")
  public @Nonnull RegistrationPage goToRegistrationPage() {
    registerBtn.click();
    return new RegistrationPage();
  }
}
