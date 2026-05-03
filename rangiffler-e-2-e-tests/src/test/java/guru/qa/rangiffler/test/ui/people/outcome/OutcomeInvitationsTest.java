package guru.qa.rangiffler.test.ui.people.outcome;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.MINOR;
import static io.qameta.allure.SeverityLevel.TRIVIAL;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.model.enums.PeopleTabSelector;
import guru.qa.rangiffler.page.PeoplePage;
import guru.qa.rangiffler.page.components.PeopleTab;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Feature("Outcome Invitations")
@DisplayName(TestPrefix.UI + TestIcon.PEOPLE + "Outcome Invitations")
@ParametersAreNonnullByDefault
public class OutcomeInvitationsTest {

  private PeopleTab peopleTab;

  @BeforeEach
  void before() {
    peopleTab = PeoplePage.open()
        .openTab(PeopleTabSelector.OUTCOME);
  }

  @User
  @Test
  @ApiLogin
  @Severity(BLOCKER)
  @DisplayName(TestPrefix.UI_POSITIVE + "Отображение пустой страницы")
  void shouldBeEmptyPage() {
    peopleTab.checkThatPageLoaded()
        .checkThatPageLoaded()
        .checkNoUsersMessage()
        .assertPeople();
  }

  @User(outcomeInvitations = 1)
  @Test
  @ApiLogin
  @Severity(BLOCKER)
  @DisplayName(TestPrefix.UI_POSITIVE + "Отображение исходящих заявок в друзья")
  void shouldBeIsPresentOutcomeInvitations(final UserJson user) {
    final UserJson outcome = user.getTestData().outcome().getFirst();
    peopleTab.checkThatPageLoaded()
        .assertPeople(outcome)
        .checkWaiting(outcome);
  }

  @User(outcomeInvitations = 2)
  @Test
  @ApiLogin
  @Severity(MINOR)
  @DisplayName(TestPrefix.UI_POSITIVE + "Поиск заявки в друзья")
  void shouldBeIsPresentOutcomeInvitationsBySearch(final UserJson user) {
    final UserJson outcome = user.getTestData().outcome().getFirst();
    peopleTab.search(outcome.getUsername())
        .assertPeople(outcome)
        .checkWaiting(outcome);
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
}
