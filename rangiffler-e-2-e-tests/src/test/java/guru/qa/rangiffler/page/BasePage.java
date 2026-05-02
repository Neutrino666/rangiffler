package guru.qa.rangiffler.page;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.config.Config;
import io.qameta.allure.Step;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

  protected static final Config CFG = Config.getInstance();

  protected final SelenideElement snackbar = $(".MuiAlert-message").as("Alert");

  @SuppressWarnings("unchecked")
  @Step("Check snackbar exist: '{text}'")
  public T checkSnackbarText(String text) {
    snackbar.shouldHave(text(text));
    return (T) this;
  }
}
