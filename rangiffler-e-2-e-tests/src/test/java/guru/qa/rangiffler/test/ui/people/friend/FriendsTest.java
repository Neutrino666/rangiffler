package guru.qa.rangiffler.test.ui.people.friend;

import static guru.qa.rangiffler.model.enums.PeopleTabSelector.FRIENDS;
import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static io.qameta.allure.SeverityLevel.TRIVIAL;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
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
@Feature("Friends")
@DisplayName(TestPrefix.UI + PeoplePage.ICON + "Friends")
@ParametersAreNonnullByDefault
public class FriendsTest {

  private PeopleTab peopleTab;

  @BeforeEach
  void before() {
    peopleTab = PeoplePage.open()
        .openTab(FRIENDS);
  }

  @User
  @Test
  @ApiLogin
  @Severity(BLOCKER)
  @DisplayName(TestPrefix.UI_POSITIVE + "Отображение пустой страницы")
  void shouldBeEmptyPage() {
    peopleTab.checkThatPageLoaded()
        .assertPeople();
  }

  @User(friends = 1)
  @Test
  @ApiLogin
  @Severity(BLOCKER)
  @DisplayName(TestPrefix.UI_POSITIVE + "Удаление друга")
  void shouldBeEmptyPage(UserJson user) {
    UserJson friend = user.getTestData().friends().getFirst();
    peopleTab.checkThatPageLoaded()
        .changeFriendship(friend, CrudButton.REMOVE)
        .goPeoplePage()
        .checkSnackbarText("Friend deleted")
        .refresh()
        .openTab(FRIENDS)
        .assertPeople();
  }

  @User(friends = 1)
  @Test
  @ApiLogin
  @Severity(BLOCKER)
  @DisplayName(TestPrefix.UI_POSITIVE + "Список добавленных друзей")
  void shouldBeFriends(UserJson user) {
    peopleTab.checkThatPageLoaded()
        .assertPeople(user.getTestData().friends());
  }

  @User(friends = 2)
  @Test
  @ApiLogin
  @Severity(NORMAL)
  @DisplayName(TestPrefix.UI_POSITIVE + "Поиск из добавленных друзей")
  void shouldBeFindFriendBySearch(UserJson user) {
    UserJson friend = user.getTestData().friends().getFirst();
    peopleTab.checkThatPageLoaded()
        .search(friend.getUsername())
        .assertPeople(friend);
  }

  @User(friends = 1)
  @Test
  @ApiLogin
  @Severity(TRIVIAL)
  @DisplayName(TestPrefix.UI_NEGATIVE + "Поиск из добавленных друзей по пробелу")
  void shouldBeFindFriendBySearch() {
    peopleTab.checkThatPageLoaded()
        .search(" ")
        .assertPeople()
        .checkNoUsersMessage();
  }
}
