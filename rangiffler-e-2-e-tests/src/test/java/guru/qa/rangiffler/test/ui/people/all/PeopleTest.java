package guru.qa.rangiffler.test.ui.people.all;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.TRIVIAL;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.PeoplePage;
import guru.qa.rangiffler.page.components.PeopleTab;
import guru.qa.rangiffler.model.enums.PeopleTabSelector;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Feature("People")
@DisplayName(TestPrefix.UI + PeoplePage.ICON + "People")
@ParametersAreNonnullByDefault
public class PeopleTest {

  private PeopleTab peopleTab;

  @BeforeEach
  void before() {
    peopleTab = PeoplePage.open()
        .openTab(PeopleTabSelector.ALL);
  }

  @User(emptyPeople = 1)
  @Test
  @ApiLogin
  @Severity(BLOCKER)
  @DisplayName(TestPrefix.UI_POSITIVE + "Отображается страница с друзьями")
  void shouldBeUsers() {
    peopleTab.checkThatPageLoaded()
        .existPeople();
  }

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_NEGATIVE + "Поиск текущего пользователя")
  void shouldBeEmptySearchBySelfUsername(final UserJson user) {
    peopleTab.search(user.getUsername())
        .assertPeople();
  }

  @User
  @Test
  @ApiLogin
  @Severity(TRIVIAL)
  @DisplayName(TestPrefix.UI_NEGATIVE + "Поиск по пробелу")
  void shouldBeEmptySearchWhenInputSpace() {
    peopleTab.search(" ")
        .assertPeople()
        .checkNoUsersMessage();
  }

  @User(friends = 1)
  @Test
  @ApiLogin
  @Severity(TRIVIAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Поиск друга")
  void shouldBeEmptyFriend(final UserJson user) {
    final UserJson friend = user.getTestData().friends().getFirst();
    peopleTab.search(friend.getUsername())
        .assertPeople(friend);
  }

  @User(emptyPeople = 1)
  @Test
  @ApiLogin
  @Severity(TRIVIAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Поиск пользователя без заявок")
  void shouldBeEmptyUser(final UserJson user) {
    final UserJson emptyUser = user.getTestData().notFriends().getFirst();
    peopleTab.search(emptyUser.getUsername())
        .assertPeople(emptyUser);
  }

  @User(incomeInvitations = 1)
  @Test
  @ApiLogin
  @Severity(TRIVIAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Поиск пользователя с Income invitations")
  void shouldBeIncomeInvitationUser(final UserJson user) {
    final UserJson income = user.getTestData().income().getFirst();
    peopleTab.search(income.getUsername())
        .assertPeople(income);
  }

  @User(outcomeInvitations = 1)
  @Test
  @ApiLogin
  @Severity(TRIVIAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Поиск пользователя с Outcome invitations")
  void shouldBeIncomeOutcomeInvitationUser(final UserJson user) {
    final UserJson outcome = user.getTestData().outcome().getFirst();
    peopleTab.search(outcome.getUsername())
        .assertPeople(outcome);
  }
}
