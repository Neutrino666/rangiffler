package guru.qa.rangiffler.test.ui;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.helpers.TestPrefix;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.page.PeoplePage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Severity(BLOCKER)
@Feature("People")
@DisplayName(TestPrefix.UI + "People " + PeoplePage.ICON)
@ParametersAreNonnullByDefault
public class PeopleTest {

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "Просмотр страницы")
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    PeoplePage.open().checkThatPageLoaded();
  }
}
