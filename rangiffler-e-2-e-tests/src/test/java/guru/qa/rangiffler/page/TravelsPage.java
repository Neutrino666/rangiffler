package guru.qa.rangiffler.page;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.page.components.Header;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class TravelsPage extends BasePage<TravelsPage> {

  public static final @Nonnull String URL = CFG.frontUrl() + "my-travels";

  private final SelenideElement self = $(".MuiContainer-maxWidthLg").as(TestIcon.TRAVELS);
  private final SelenideElement map = self.$("div .worldmap__figure-container").as("Карта: " + TestIcon.TRAVELS);
  private final SelenideElement addPhotoBtn = self.$("div .worldmap__figure-container + div button")
      .as("Кнопка: Add photo");
  private final ElementsCollection travelsSwitchButtons = self.$$(".MuiToggleButtonGroup-root button")
      .as("Кнопки: с/без друзей");
  private final SelenideElement title = $(".MuiTypography-h4").as(this.getClass().getSimpleName() + " title");
  private final ElementsCollection prevNextButtons = self.$$(".MuiGrid-spacing-xs-3 + div button")
      .as("Кнопки: previous и next");
  private final ElementsCollection photoCards = self.$$(".MuiPaper-elevation").as("Список фото");

  @Getter
  private final Header header = new Header();

  @Step(TestIcon.TRAVELS + "Проверка загрузки")
  public @Nonnull TravelsPage checkThatPageLoaded() {
    SelenideUtils.visible(self, map, addPhotoBtn, title);
    travelsSwitchButtons.shouldHave(size(2))
        .filterBy(visible)
        .shouldHave(size(2));
    header.checkThatComponentLoaded();
    return this;
  }

  @Step(TestIcon.TRAVELS + "Открываем")
  public static @Nonnull TravelsPage open() {
    return Selenide.open(URL, TravelsPage.class);
  }
}
