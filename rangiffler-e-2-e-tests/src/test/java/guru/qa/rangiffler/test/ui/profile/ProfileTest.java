package guru.qa.rangiffler.test.ui.profile;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.model.TestIcon;
import guru.qa.rangiffler.model.TestPrefix;
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
@Feature("Profile")
@DisplayName(TestPrefix.UI + TestIcon.PROFILE + "Profile")
@ParametersAreNonnullByDefault
public class ProfileTest {

  @User(incomeInvitations = 2)
  @Test
  @ApiLogin
  @DisplayName(TestPrefix.UI_POSITIVE + "Просмотр страницы")
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    ProfilePage.open()
        .checkThatPageLoaded()
        .checkIsPresentFallbackAvatar();
  }
}
