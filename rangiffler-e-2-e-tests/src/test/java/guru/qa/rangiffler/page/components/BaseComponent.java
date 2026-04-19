package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.SelenideElement;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<?>> {

  protected final SelenideElement self;

  public BaseComponent(final SelenideElement self) {
    this.self = self;
  }
}
