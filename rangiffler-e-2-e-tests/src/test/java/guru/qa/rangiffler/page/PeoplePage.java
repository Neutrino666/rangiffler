package guru.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.components.Header;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class PeoplePage extends BasePage<PeoplePage> {

  public static final String ICON = "\uD83D\uDC64 \uD83D\uDC64 \uD83D\uDC64 ";
  public static final String url = CFG.frontUrl() + "people";

  private final SelenideElement self = $(".MuiContainer-maxWidthLg")
      .as(ICON);

  @Getter
  private final Header header = new Header();

  @Step(ICON + "Открываем")
  public static @Nonnull PeoplePage open() {
    return Selenide.open(url, PeoplePage.class);
  }

  @Step(ICON + "Проверка загрузки")
  public @Nonnull PeoplePage checkThatPageLoaded() {
    self.shouldBe(visible);
    header.checkThatComponentLoaded();
    return this;
  }


}
