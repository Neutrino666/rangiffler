package guru.qa.rangiffler.page;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.enums.PeopleTabSelector;
import guru.qa.rangiffler.page.components.Header;
import guru.qa.rangiffler.page.components.PeopleTab;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class PeoplePage extends BasePage<PeoplePage> {

  public static final @Nonnull String url = CFG.frontUrl() + "people";

  private final SelenideElement self = $("[role='tablist']").as(TestIcon.PEOPLE);
  private final ElementsCollection tabButtons = self.$$("button").as("Tab buttons");

  @Getter
  private final Header header = new Header();

  @Step(TestIcon.PEOPLE + "Открываем")
  public static @Nonnull PeoplePage open() {
    return Selenide.open(url, PeoplePage.class);
  }

  @Step(TestIcon.PEOPLE + "Проверка загрузки")
  public @Nonnull PeoplePage checkThatPageLoaded() {
    SelenideUtils.visible(self);
    tabButtons.shouldHave(size(PeopleTabSelector.values().length));
    header.checkThatComponentLoaded();
    return this;
  }

  @Step(TestIcon.PEOPLE + "Открываем вкладку {tabSelector}")
  public @Nonnull PeopleTab openTab(PeopleTabSelector tabSelector) {
    findTab(tabSelector).click();
    return new PeopleTab(tabSelector);
  }

  private SelenideElement findTab(PeopleTabSelector tabSelector) {
    return tabButtons.filterBy(matchText("^" + tabSelector.name()))
        .shouldHave(size(1))
        .get(0)
        .as("Tab " + tabSelector);
  }
}
