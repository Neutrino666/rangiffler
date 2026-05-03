package guru.qa.rangiffler.test.ui.people.income;

import static guru.qa.rangiffler.model.enums.PeopleTabSelector.INCOME;
import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static io.qameta.allure.SeverityLevel.TRIVIAL;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.PeoplePage;
import guru.qa.rangiffler.page.components.PeopleTab;
import guru.qa.rangiffler.page.components.PeopleTab.CrudButton;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Severity(BLOCKER)
@Feature("Income Invitations")
@DisplayName(TestPrefix.UI + TestIcon.PEOPLE + "Income Invitations")
@ParametersAreNonnullByDefault
public class IncomeInvitationsTest {

  private PeopleTab peopleTab;

  @BeforeEach
  void before() {
    peopleTab = PeoplePage.open()
        .openTab(INCOME);
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

  @User(incomeInvitations = 1)
  @Test
  @ApiLogin
  @Severity(BLOCKER)
  @DisplayName(TestPrefix.UI_POSITIVE + "Список входящих заявок")
  void shouldBeIncomeInvitations(UserJson user) {
    List<UserJson> incomeInvitation = user.getTestData().income();
    peopleTab.checkThatPageLoaded()
        .assertPeople(incomeInvitation)
        .checkButtonsByUser(incomeInvitation.getFirst(), CrudButton.ACCEPT, CrudButton.DECLINE);
  }

  @User(incomeInvitations = 2)
  @Test
  @ApiLogin
  @Severity(NORMAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Поиск из входящих заявок")
  void shouldBeFindFriendBySearch(UserJson user) {
    UserJson incomeInvitation = user.getTestData().income().getFirst();
    peopleTab.checkThatPageLoaded()
        .search(incomeInvitation.getUsername())
        .assertPeople(incomeInvitation)
        .checkButtonsByUser(incomeInvitation, CrudButton.ACCEPT, CrudButton.DECLINE);
  }

  @User(incomeInvitations = 1)
  @Test
  @ApiLogin
  @Severity(BLOCKER)
  @DisplayName(TestPrefix.UI_POSITIVE + "Принятие запроса дружбы")
  void acceptFriendShouldBeSuccess(UserJson user) {
    UserJson userForUpdate = user.getTestData().income().getFirst();
    peopleTab.checkThatPageLoaded()
        .changeFriendship(userForUpdate, CrudButton.ACCEPT)
        .goPeoplePage()
        .checkSnackbarText("Invitation accepted")
        .refresh()
        .openTab(INCOME)
        .checkNoUsersMessage()
        .search(userForUpdate.getUsername())
        .assertPeople()
        .checkNoUsersMessage();
  }

  @User(incomeInvitations = 1)
  @Test
  @ApiLogin
  @Severity(BLOCKER)
  @DisplayName(TestPrefix.UI_POSITIVE + "Отклонение запроса дружбы")
  void declineFriendShouldBeSuccess(final UserJson user) {
    final UserJson userForUpdate = user.getTestData().income().getFirst();
    peopleTab.checkThatPageLoaded()
        .search(userForUpdate.getUsername())
        .changeFriendship(userForUpdate, CrudButton.DECLINE)
        .goPeoplePage()
        .checkSnackbarText("Invitation declined")
        .refresh()
        .openTab(INCOME)
        .checkNoUsersMessage()
        .search(userForUpdate.getUsername())
        .assertPeople()
        .checkNoUsersMessage();
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
