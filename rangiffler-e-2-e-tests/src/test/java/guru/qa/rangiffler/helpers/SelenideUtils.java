package guru.qa.rangiffler.helpers;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.visible;

import com.codeborne.selenide.SelenideElement;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class SelenideUtils {

  public static void visibleAndInteractable(SelenideElement... els) {
    Stream.of(els).forEach(el -> el.shouldBe(visible, interactable));
  }

  public static void visible(SelenideElement... els) {
    Stream.of(els).forEach(el -> el.shouldBe(visible));;
  }

  public static void exist(SelenideElement... els) {
    Stream.of(els).forEach(el -> el.shouldBe(exist));
  }
}
