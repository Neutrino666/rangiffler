package guru.qa.rangiffler.page;

import guru.qa.rangiffler.page.components.Header;
import io.qameta.allure.Step;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;

@ParametersAreNonnullByDefault
public final class MainPage extends BasePage<MainPage> {

  public static final String URL = CFG.frontUrl() + "my-travels";
  @Getter
  private final Header header = new Header();

  @Step("Проверка загрузки главной страницы")
  public @Nonnull MainPage checkThatPageLoaded() {
    return this;
  }
}
