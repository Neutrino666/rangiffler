package guru.qa.rangiffler.page.components;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.page.auth.LoginPage;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class Header extends BaseComponent<Header> {

  private final SelenideElement menuBtn = self.$(".MuiIconButton-edgeStart")
      .as("Кнопка раскрытия/сокрытия sidebar");
  private final SelenideElement exitBtn = self.$("button[ aria-label = 'Logout' ]").as("Кнопка выхода из сайта");
  private final SelenideElement mainPageLink = self.$(".link").as("Ссылка перехода на главную страницу");

  public Header() {
    super($(".MuiToolbar-root").as("[Компонент] Заголовок сайта"));
  }

  @Step("Выход")
  public @Nonnull LoginPage exit() {
    exitBtn.click();
    return new LoginPage();
  }

  @Step("Проверяем что страница прогрузилась")
  public @Nonnull Header checkThatComponentLoaded() {
    self.shouldBe(visible);
    SelenideUtils.visibleAndInteractable(menuBtn, exitBtn, mainPageLink);
    return this;
  }
}
