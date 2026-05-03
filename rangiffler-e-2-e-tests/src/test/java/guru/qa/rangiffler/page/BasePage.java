package guru.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.helpers.ScreenDiffResult;
import guru.qa.rangiffler.model.TestIcon;
import io.qameta.allure.Step;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

  protected static final @Nonnull Config CFG = Config.getInstance();

  protected final @Nonnull SelenideElement snackbar = $(".MuiAlert-message").as("Alert");

  @SuppressWarnings("unchecked")
  @Step(TestIcon.SNACKBAR + "Check snackbar exist: '{text}'")
  public T checkSnackbarText(String text) {
    snackbar.shouldHave(text(text));
    return (T) this;
  }

  protected void assertScreen(BufferedImage expected, SelenideElement actualLocator) {
    assertScreen(expected, actualLocator, 0);
  }

  protected void assertScreen(BufferedImage expected, SelenideElement actualLocator,
      Integer waitMills) {
    Selenide.sleep(waitMills);
    try {
      BufferedImage actual = ImageIO.read(Objects.requireNonNull(
              $(actualLocator).screenshot()
          )
      );
      assertThat(new ScreenDiffResult(
          expected, actual
      ).getAsBoolean())
          .describedAs(
              "Отличия в скриншоте не превышают допустимую погрешность: %spx",
              ScreenDiffResult.ALLOWED_DIFF_PIXELS
          )
          .isFalse();
    } catch (IOException e) {
      throw new RuntimeException("Screen comparison failure: " + e);
    }
  }

  @SuppressWarnings("unchecked")
  public T refresh() {
    Selenide.refresh();
    return (T) this;
  }
}
