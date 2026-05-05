package guru.qa.rangiffler.condition;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import guru.qa.rangiffler.model.PhotoJson;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.opentest4j.AssertionFailedError;

@ParametersAreNonnullByDefault
public class PhotoCardCondition {

  public static @Nonnull WebElementsCondition exactlyPhotoCardsInAnyOrder(PhotoJson... photos) {
    return new WebElementsCondition() {
      private final List<List<String>> expected = Stream.of(photos).map(PhotoJson::toExpectedCondition).toList();

      @Override
      public @Nonnull String toString() {
        return expected.toString();
      }

      @Override
      public @Nonnull CheckResult check(Driver driver, List<WebElement> elements) {
        List<List<String>> actual = convertToActual(elements);
        if (expected.size() != actual.size()) {
          String message = String.format("List size mismatch (expected: %s, actual: %s)",
              expected.size(), actual.size());
          return rejected(message, actual.toString());
        }
        if (!actual.containsAll(expected)) {
          final String message = String.format("List photos mismatch (expected: %s, actual: %s)",
              expected, actual);
          return rejected(message, actual.toString());
        }
        return accepted();
      }

      @Override
      public void fail(CollectionSource collection, CheckResult lastCheckResult,
          @Nullable Exception cause, long timeoutMs) {
        try {
          assertThat((String) lastCheckResult.getActualValue())
              .as("Все фото путешествий должны присутствовать в любой последовательности")
              .isEqualTo(toString());
        } catch (AssertionFailedError e) {
          throw new UIAssertionError(
              e.getMessage(),
              toString(), lastCheckResult.getActualValue()
          );
        }
      }
    };
  }

  private static @Nonnull List<List<String>> convertToActual(final List<WebElement> elements) {
    return elements
        .stream()
        .map(PhotoCardCondition::photoCard)
        .toList();
  }

  @Nonnull
  private static List<String> photoCard(WebElement el) {
    return List.of(
        Objects.requireNonNull(el.findElement(By.className("photo-card__image"))
            .getAttribute("src")),
        el.findElement(By.cssSelector(".MuiTypography-body2")).getText(),
        el.findElement(By.cssSelector(".MuiTypography-subtitle1")).getText(),
        el.findElement(By.cssSelector(".photo-card__content")).getText()
    );
  }
}
