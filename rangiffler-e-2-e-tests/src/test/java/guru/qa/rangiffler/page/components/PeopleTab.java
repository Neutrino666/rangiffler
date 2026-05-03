package guru.qa.rangiffler.page.components;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rangiffler.condition.PeopleCondition.containsAllPeopleInAnyOrder;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.helpers.SelenideUtils;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.model.enums.PeopleTabSelector;
import guru.qa.rangiffler.page.PeoplePage;
import io.qameta.allure.Step;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ParametersAreNonnullByDefault
public final class PeopleTab extends BaseComponent<Header> {

  private final SelenideElement searchInput = self.$("input").as("Поисковое поле");
  private final SelenideElement submitBtn = self.$("[type='submit']").as("Кнопка поиска");
  private final SelenideElement table = self.$("table").as("Таблица");
  private final SelenideElement empty = self.$(".MuiTypography-h6").as("No users message");

  private final ElementsCollection headers = table.$$(".MuiTableRow-head th").as("Заголовки таблицы");
  private final ElementsCollection prevNextButtons = self.$$("table + div")
      .as("Кнопки: previous и next");
  private final ElementsCollection people = table.$$("tbody tr").as("Пользователи: Список строк");

  @Getter
  @RequiredArgsConstructor
  public enum CrudButton {
    ADD("Add"),
    ACCEPT("Accept"),
    DECLINE("Decline"),
    REMOVE("Remove");

    private final String value;

    @Override
    public String toString() {
      return this.getClass().getSimpleName() + "." + name();
    }
  }

  @Getter
  @RequiredArgsConstructor
  @ParametersAreNonnullByDefault
  enum TableColumn {
    AVATAR("Avatar"),
    USERNAME("Username"),
    NAME("Name"),
    SURNAME("Surname"),
    LOCATION("location"),
    ACTIONS("Actions");

    private final String value;

    @Override
    public String toString() {
      return this.getClass().getSimpleName() + "." + name();
    }
  }

  public PeopleTab(PeopleTabSelector selector) {
    super($(selector.getValue()));
  }

  @Step(TestIcon.PEOPLE + "Проверка загрузки")
  public @Nonnull PeopleTab checkThatPageLoaded() {
    SelenideUtils.visible(self);
    SelenideUtils.visibleAndInteractable(submitBtn);
    List<String> columns = Stream.of(TableColumn.values()).map(TableColumn::getValue).toList();
    headers.shouldHave(texts(columns));
    return this;
  }

  public PeoplePage goPeoplePage() {
    return new PeoplePage();
  }

  @Step(TestIcon.PEOPLE + "Проверка наполнения таблицы")
  public PeopleTab assertPeople(List<UserJson> expectedPeople) {
    people.shouldHave(containsAllPeopleInAnyOrder(expectedPeople));
    return this;
  }

  public PeopleTab assertPeople(UserJson... expectedPeople) {
    return assertPeople(Stream.of(expectedPeople).toList());
  }

  @Step(TestIcon.PEOPLE + "Вбиваем в поисковую строку '{text}'")
  public PeopleTab search(final String text) {
    searchInput.setValue(text);
    submitBtn.click();
    return this;
  }

  @Step(TestIcon.PEOPLE + "Проверка сообщения о не найденных пользователях")
  public PeopleTab checkNoUsersMessage() {
    empty.shouldHave(text("There are no users yet"));
    return this;
  }

  @Step(TestIcon.PEOPLE + "Проверка присутствуют пользователи")
  public PeopleTab existPeople() {
    people.shouldHave(sizeGreaterThanOrEqual(1));
    return this;
  }

  @Step(TestIcon.PEOPLE + "Изменение статуса дружбы на '{button}'")
  public PeopleTab changeFriendship(final UserJson targetUser, CrudButton button) {
    findUserInTable(targetUser).$$("button")
        .findBy(text(button.name()))
        .click();
    return this;
  }

  @Step(TestIcon.PEOPLE + "Изменение статуса дружбы на '{button}'")
  public PeopleTab checkWaiting(final UserJson targetUser) {
    findUserInTable(targetUser).$$("td").last().shouldHave(text("Waiting..."), visible);
    return this;
  }

  @Step(TestIcon.PEOPLE + "Изменение статуса дружбы на '{button}'")
  public PeopleTab checkButtonsByUser(final UserJson targetUser, final CrudButton... buttons) {
    List<String> expectedTexts = Stream.of(buttons).map(CrudButton::getValue).toList();
    findUserInTable(targetUser).$$("button")
        .shouldHave(exactTexts(expectedTexts));
    return this;
  }

  private SelenideElement findUserInTable(final UserJson user) {
    String pattern = "^%s %s.+$".formatted(user.getUsername(), user.getCountry().name());
    return people.findBy(matchText(pattern));
  }
}
