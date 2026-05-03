package guru.qa.rangiffler.test.ui.auth;

import static io.qameta.allure.SeverityLevel.BLOCKER;

import guru.qa.rangiffler.model.TestPrefix;
import guru.qa.rangiffler.jupiter.meta.WebTest;
import guru.qa.rangiffler.page.auth.WelcomePage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@WebTest
@Tag("smoke")
@Severity(BLOCKER)
@Feature("Приветственная страница")
@DisplayName(TestPrefix.UI + WelcomePage.ICON + "Welcome")
@ParametersAreNonnullByDefault
public class WelcomePageTest {

  @Test
  @DisplayName(TestPrefix.UI_POSITIVE + "Отображение приветственной страницы")
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    WelcomePage.open().checkThatPageLoaded();
  }
}
