package guru.qa.rangiffler.test.ui;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.helpers.TestPrefix;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.page.ProfilePage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Severity(BLOCKER)
@Feature("Профиль")
@DisplayName(TestPrefix.UI + "Профиль " + ProfilePage.ICON)
@ParametersAreNonnullByDefault
public class ProfileTest {

  @User
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "Просмотр страницы")
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    ProfilePage.open().checkThatPageLoaded();
  }
}
