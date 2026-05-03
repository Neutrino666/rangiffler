package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.SelenideElement;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<?>> {

  protected final @Nonnull SelenideElement self;

  public BaseComponent(final SelenideElement self) {
    this.self = self;
  }
}
