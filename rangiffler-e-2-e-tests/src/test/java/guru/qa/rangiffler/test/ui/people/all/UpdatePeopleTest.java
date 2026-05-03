package guru.qa.rangiffler.test.ui.people.all;

import static guru.qa.rangiffler.model.enums.PeopleTabSelector.ALL;
import static io.qameta.allure.SeverityLevel.NORMAL;

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
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Feature("People")
@DisplayName(TestPrefix.UI + TestIcon.PEOPLE + "People")
@ParametersAreNonnullByDefault
public class UpdatePeopleTest {

  private PeopleTab peopleTab;

  @BeforeEach
  void before() {
    peopleTab = PeoplePage.open()
        .openTab(ALL);
  }

  @User(emptyPeople = 1)
  @Test
  @ApiLogin
  @Severity(NORMAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Создание запроса дружбы")
  void sendRequestShouldBeSuccess(final UserJson user) {
    final UserJson emptyUser = user.getTestData().notFriends().getFirst();
    peopleTab.checkThatPageLoaded()
        .search(emptyUser.getUsername())
        .changeFriendship(emptyUser, CrudButton.ADD);
    new PeoplePage().checkSnackbarText("Invitation sent");
    peopleTab.checkWaiting(emptyUser)
        .assertPeople(emptyUser);
  }

  @User(incomeInvitations = 1)
  @Test
  @ApiLogin
  @Severity(NORMAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Принятие запроса дружбы")
  void acceptFriendShouldBeSuccess(final UserJson user) {
    final UserJson incomeInvitation = user.getTestData().income().getFirst();
    peopleTab.checkThatPageLoaded()
        .search(incomeInvitation.getUsername())
        .changeFriendship(incomeInvitation, CrudButton.ACCEPT)
        .goPeoplePage()
        .checkSnackbarText("Invitation accepted")
        .refresh()
        .openTab(ALL)
        .search(incomeInvitation.getUsername())
        .assertPeople(incomeInvitation)
        .checkButtonsByUser(incomeInvitation, CrudButton.REMOVE);
  }

  @User(incomeInvitations = 1)
  @Test
  @ApiLogin
  @Severity(NORMAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Отклонение запроса дружбы")
  void declineFriendShouldBeSuccess(final UserJson user) {
    final UserJson incomeInvitation = user.getTestData().income().getFirst();
    peopleTab.checkThatPageLoaded()
        .search(incomeInvitation.getUsername())
        .changeFriendship(incomeInvitation, CrudButton.DECLINE)
        .goPeoplePage()
        .checkSnackbarText("Invitation declined")
        .refresh()
        .openTab(ALL)
        .search(incomeInvitation.getUsername())
        .assertPeople(incomeInvitation)
        .checkButtonsByUser(incomeInvitation, CrudButton.ADD);
  }

  @User(friends = 1)
  @Test
  @ApiLogin
  @Severity(NORMAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Удаление друга")
  void removeFriendShouldBeSuccess(final UserJson user) {
    final UserJson updatedUser = user.getTestData().friends().getFirst();
    peopleTab.checkThatPageLoaded()
        .search(updatedUser.getUsername())
        .changeFriendship(updatedUser, CrudButton.REMOVE)
        .goPeoplePage()
        .checkSnackbarText("Friend deleted")
        .refresh()
        .openTab(ALL)
        .search(updatedUser.getUsername())
        .assertPeople(updatedUser)
        .checkButtonsByUser(updatedUser, CrudButton.ADD);
  }
}
